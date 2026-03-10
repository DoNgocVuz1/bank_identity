package com.example.bank_identity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  UserDTO {
    private Long id;
    private String username;
    private String role;
    private Boolean enabled;
    //ko trả pass do bảo mật
}
