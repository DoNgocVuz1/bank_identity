package com.example.bank_identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "User không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3-50 ký tự")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 8, message = "Password phải ít nhất 8 ký tự")
    private String password;

    @NotBlank(message = "Role không được để trống")
    @Pattern(regexp = "USER|ADMIN",message = "Role chỉ là User hoặc Admin")
    private String role;

    private Boolean enabled = true;//Mặc định cái này là true
}
