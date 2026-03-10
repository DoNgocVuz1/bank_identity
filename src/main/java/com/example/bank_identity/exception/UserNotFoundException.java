package com.example.bank_identity.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("Không tìm thấy user với username: " + username);
    }
}