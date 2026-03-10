package com.example.bank_identity.controller;

import com.example.bank_identity.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;  // ← @Controller, KHÔNG phải @RestController
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller               // trả về tên file HTML, không trả JSON
@RequestMapping("/web")
public class WebController {

    // GET /web/login → hiển thị trang login
    @GetMapping("/login")
    public String loginPage() {
        return "login";   // → tìm file templates/login.html
    }

    // GET /web/dashboard → trang sau khi đăng nhập
    // @AuthenticationPrincipal inject thông tin user từ Session
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return "dashboard"; // → tìm file templates/dashboard.html
    }

    // GET /web/admin → chỉ ADMIN
    @GetMapping("/admin")
    public String adminPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        return "admin";   // → tìm file templates/admin.html
    }
}