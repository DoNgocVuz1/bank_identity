package com.example.bank_identity.controller;

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

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("api/test")
@RequiredArgsConstructor
public class TestController {
    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint() {
        return ResponseEntity.ok(new MessageResponse(
                "Đây là endpoint BẢO VỆ  Bạn cần JWT token để truy cập!"
        ));
    }
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
    @GetMapping("/hello")
    public ResponseEntity<?> hello(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new MessageResponse(
                "Xin chào " + userDetails.getUsername() + "! Bạn đã authenticated."
        ));
    }
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(new MessageResponse("API hoạt động!"));
    }
    @GetMapping("/admin-only")
    public ResponseEntity<?> adminOnly(){
        return ResponseEntity.ok("Chỉ admin mới truy cập được");
    }
}
