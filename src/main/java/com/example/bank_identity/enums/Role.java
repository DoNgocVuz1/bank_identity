package com.example.bank_identity.enums;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    public String getValue(){
        return this.name();
    }
}
