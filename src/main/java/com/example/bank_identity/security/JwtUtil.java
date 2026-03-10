package com.example.bank_identity.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        String role = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Long userId = userPrincipal.getId();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .claim("role", role)        // Thêm role vào token
                .claim("userId", userId)    // Thêm userId vào token
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public long getExpirationFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .getTime();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey()) // dùng secret key để verify chữ ký
                    .build()
                    .parseSignedClaims(authToken);// parse và check luôn
            return true;//ok rồi thì trả

        } catch (MalformedJwtException e) { // token sai format, không phải JWT hợp lệ
            log.warn("[JwtUtil] Token sai format: {}", e.getMessage());
        } catch (ExpiredJwtException e) {// token hết hạn
            log.warn("[JwtUtil] Token đã hết hạn: {}",e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("[JwtUtil] Token không được hỗ trợ: {}",e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("[JwtUtil] Token rỗng hoặc null: {}", e.getMessage());
        } catch (SignatureException e) { // chữ ký sai, có thể bị giả mạo
            log.warn("[JwtUtil] Chữ ký token không hợp lệ: {}", e.getMessage());
        }
        return false;// có lỗi, không hợp lệ
    }
}