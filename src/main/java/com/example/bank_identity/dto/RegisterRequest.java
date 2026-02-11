package com.example.bank_identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request body cho API đăng ký tài khoản")
public class RegisterRequest {

    @Schema(description = "Tên đăng nhập (3-50 ký tự)", example = "newuser", required = true)
    @NotBlank(message = "Vui lòng nhập tên đăng nhập")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3-50 ký tự")
    private String username;

    @Schema(description = "Mật khẩu (tối thiểu 8 ký tự)", example = "password123", required = true)
    @NotBlank(message = "Vui lòng nhập mật khẩu")
    @Size(min = 8, message = "Mật khẩu tối thiểu 8 kí tự")
    private  String password;
}
