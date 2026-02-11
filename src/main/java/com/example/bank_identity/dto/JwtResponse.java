package com.example.bank_identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@Schema(description = "Response trả về khi đăng nhập thành công")
public class JwtResponse {

    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Loại token", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Tên đăng nhập", example = "admin")
    private String username;

    @Schema(description = "Vai trò của user", example = "ROLE_ADMIN")
    private  String role;

    public JwtResponse(String token, String username, String role){
        this.token = token;
        this.username = username;
        this.role = role;
    }
}
