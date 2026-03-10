package com.example.bank_identity.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Size(min = 8, message = "Password phải ít nhất 8 ký tự")
    private String password;//Nullble chỉ update nếu muốn đổi password

    @Pattern(regexp = "USER|ADMIN", message = "Role chỉ là User hoặc Admin")
    private String role;

    private Boolean enabled;//chỉ update khi muốn đổi trạng thái
}
