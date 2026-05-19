package com.prasadfencing.backendecom.user.controller;

import com.prasadfencing.backendecom.user.dto.UpdateUserRequest;
import com.prasadfencing.backendecom.user.dto.UserResponse;
import com.prasadfencing.backendecom.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET CURRENT USER
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    // GET ALL USERS (ADMIN ONLY)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // UPDATE USER
    @PutMapping
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    // DELETE USER
    @DeleteMapping
    public ResponseEntity<String> deleteUser() {
        return ResponseEntity.ok(userService.deleteUser());
    }
}