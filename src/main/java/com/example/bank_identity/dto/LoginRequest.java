package com.example.bank_identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request đăng nhập")
public class LoginRequest {

    @Schema(description = "Tên đăng nhập", example = "admin", required = true)
    @NotBlank(message = "Vui lòng nhập tên đăng nhập")
    private String username;

    @Schema(description = "Mật khẩu", example = "admin123", required = true)
    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private  String password;
}
