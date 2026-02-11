package com.example.bank_identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Schema(description = "Response chứa thông báo")
public class MessageResponse {

    @Schema(description = "Nội dung thông báo", example = "Đăng ký thành công!")
    private String message;
}
