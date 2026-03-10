package com.example.bank_identity.controller;

import com.example.bank_identity.security.JwtBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.example.bank_identity.security.JwtBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import com.example.bank_identity.dto.JwtResponse;
import com.example.bank_identity.dto.LoginRequest;
import com.example.bank_identity.dto.MessageResponse;
import com.example.bank_identity.dto.RegisterRequest;
import com.example.bank_identity.security.CustomUserDetails;
import com.example.bank_identity.security.JwtUtil;
import com.example.bank_identity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Tag(name = "Authentication", description = "API xác thực - đăng ký & đăng nhập")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService blacklistService;

    @Operation(summary = "Đăng ký tài khoản", description = "Tạo tài khoản user mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Username đã tồn tại hoặc thông tin không hợp lệ")

    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.registerUser(request);
            return ResponseEntity.ok(new MessageResponse("Đăng ký thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    @Operation(summary = "Đăng xuất", description = "Thu hồi JWT token")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            try {
                long expiration = jwtUtil.getExpirationFromToken(token);
                blacklistService.blacklistToken(token, expiration);
            } catch (Exception e) {
                log.warn("[Auth] Logout token không hợp lệ: {}", e.getMessage());
            }
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Đăng xuất thành công. Token đã bị thu hồi."));
    }

    @Operation(summary = "Đăng nhập", description = "Đăng nhập và nhận JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công, trả về JWT token"),
            @ApiResponse(responseCode = "401", description = "Sai username hoặc password")
    })

    //AuthenticationManager kiểm tra username/password
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),//username từ client gửi lên
                        request.getPassword() //password từ client gửi lên
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //nếu username/password đúng thì bắt đầu tạo jwt trả về cho client
        String jwt = jwtUtil.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority()
        ));
    }
}