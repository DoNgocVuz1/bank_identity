package com.example.bank_identity.controller;

import com.example.bank_identity.dto.CreateUserRequest;
import com.example.bank_identity.dto.MessageResponse;
import com.example.bank_identity.dto.UpdateUserRequest;
import com.example.bank_identity.dto.UserDTO;
import com.example.bank_identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý User
 * - Admin: Full quyền CRUD
 * - User: Chỉ xem được thông tin của chính mình
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API quản lý user")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy tất cả users", description = "Chỉ admin mới được xem tất cả user")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{username}")
    @Operation(summary = "Xem thông tin user theo username",
            description = "Admin xem được tất cả, User chỉ xem được của mình")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        // Lấy thông tin user đang login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Kiểm tra quyền: Admin xem được tất cả, User chỉ xem được của mình
        if (!isAdmin && !currentUsername.equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Bạn không có quyền xem thông tin user này"));
        }

        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo user mới", description = "Chỉ admin mới được tạo user")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserDTO createdUser = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }


    @PutMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật user", description = "Chỉ admin mới được sửa user")
    public ResponseEntity<?> updateUser(
            @PathVariable String username,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            UserDTO updatedUser = userService.updateUser(username, request);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }


    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa user", description = "Chỉ admin mới được xóa user")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        try {
            // Không cho xóa chính mình
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();

            if (currentUsername.equals(username)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Không thể xóa chính mình"));
            }

            userService.deleteUser(username);
            return ResponseEntity.ok(new MessageResponse("Xóa user thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
}