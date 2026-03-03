package com.example.bank_identity.service;


import com.example.bank_identity.dto.CreateUserRequest;
import com.example.bank_identity.dto.UpdateUserRequest;
import com.example.bank_identity.dto.UserDTO;
import com.example.bank_identity.entity.User;
import com.example.bank_identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public UserDTO getUserByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user" + username));
        return convertToDTO(user);
    }

    @Transactional
    public UserDTO createUser(CreateUserRequest request){
        //kiểm tra xem user đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException(("Username đã tồn tại: "+ request.getUsername()));
        }

        //Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_" + request.getRole());
        user.setEnabled(request.getEnabled() != null ? request.getEnabled(): true);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(String username, UpdateUserRequest request){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("không tìm thấy user: "+ username));

        if (request.getPassword() != null && !request.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if(request.getRole() !=null && !request.getRole().isEmpty()){
            user.setRole("ROLE_"+request.getRole());
        }

        if (request.getEnabled() !=null){
            user.setEnabled(request.getEnabled());
        }

        User updateUser = userRepository.save(user);
        return convertToDTO(updateUser);
    }
    @Transactional
    public void deleteUser(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: "+username));
        userRepository.delete(user);
    }
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getEnabled()
        );
    }

}
