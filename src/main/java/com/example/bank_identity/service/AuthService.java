package com.example.bank_identity.service;


import com.example.bank_identity.dto.RegisterRequest;
import com.example.bank_identity.entity.User;
import com.example.bank_identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Tên tài khoản đã tồn tại!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnabled(true);

        return userRepository.save(user);

    }
    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }
}
