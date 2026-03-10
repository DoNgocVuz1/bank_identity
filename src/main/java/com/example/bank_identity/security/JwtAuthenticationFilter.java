package com.example.bank_identity.security;


import com.example.bank_identity.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import org.springframework.http.MediaType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
// OncePerRequestFilter tự xử lý logic này bên trong,  đảm bảo filter chỉ chạy đúng 1 lần cho mỗi request
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtBlacklistService blacklistService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;


    @Override

    protected void doFilterInternal(// Chắc chắn chỉ chạy 1 lần duy nhất
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {


        try {
            String jwt = parseJwt(request);//Lấy token từ header "Authorization: Bearer xxxx"
            //Validate token còn hạn, không bị giả mạo

            if (jwt != null && blacklistService.isBlacklisted(jwt)) {
                log.warn("[JWT-Filter] Token đã bị thu hồi | path={}", request.getRequestURI());
                // Trả 401 ngay, không cho đi tiếp
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .writeValue(response.getOutputStream(),
                                new ErrorResponse(401, "Unauthorized", "Token đã bị thu hồi, vui lòng đăng nhập lại", request.getRequestURI()));
                return;
            }

            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {

                //Lấy username từ token
                String username = jwtUtil.getUsernameFromJwtToken(jwt);

                //Load user từ db để lấy role
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // Gắn thông tin user vào SecurityContext để các bước sau dùng
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                //set nào SecurityContext để các filter sau biết user này là ai
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            System.err.println("Cannot set user authentication: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}