package com.example.bank_identity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.example.bank_identity.dto.MessageResponse;
import com.example.bank_identity.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Test Endpoints", description = "API test cho các endpoint cần authentication")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("api/test")
@RequiredArgsConstructor
public class TestController {

    @Operation(summary = "Endpoint bảo vệ - chỉ ADMIN",
            description = "Chỉ user có role ADMIN mới truy cập được")
    @PreAuthorize("hasRole('ADMIN')")//Chỉ dành riêng cho admin
    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint() {
        return ResponseEntity.ok(new MessageResponse(
                "Đây là endpoint BẢO VỆ  Bạn cần JWT token để truy cập!"
        ));
    }
    @Operation(summary = "Lấy thông tin user hiện tại",
            description = "Trả về thông tin của user đang đăng nhập")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thông tin user hiện tại");
        response.put("id", userDetails.getId());
        response.put("username", userDetails.getUsername());
        response.put("authorities", userDetails.getAuthorities());

        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Chào user", description = "API test hiển thị tên user")
    @GetMapping("/hello")
    public ResponseEntity<?> hello(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new MessageResponse(
                "Xin chào " + userDetails.getUsername() + "! Bạn đã authenticated."
        ));
    }
    @Operation(summary = "Test API", description = "Kiểm tra API có hoạt động không")
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(new MessageResponse("API hoạt động!"));
    }

    @Operation(summary = "Admin only endpoint", description = "Chỉ admin mới vào được")
    @PreAuthorize("hasRole('ADMIN')")//Chỉ dành riêng cho admin
    @GetMapping("/admin-only")
    public ResponseEntity<?> adminOnly(){
        return ResponseEntity.ok("Chỉ admin mới truy cập được");
    }
}
