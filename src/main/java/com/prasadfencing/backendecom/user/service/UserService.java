package com.prasadfencing.backendecom.user.service;

import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.exception.custom.ResourceNotFoundException;
import com.prasadfencing.backendecom.user.dto.UpdateUserRequest;
import com.prasadfencing.backendecom.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // =========================
    // GET CURRENT USER ENTITY
    // =========================
    private User getCurrentUserEntity() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    // =========================
    // GET CURRENT USER
    // =========================
    public UserResponse getCurrentUser() {
        return mapToResponse(getCurrentUserEntity());
    }

    // =========================
    // GET ALL USERS
    // =========================
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // UPDATE USER
    // =========================
    public UserResponse updateUser(UpdateUserRequest request) {

        User user = getCurrentUserEntity();

        user.setName(request.getName());

        return mapToResponse(userRepository.save(user));
    }

    // =========================
    // DELETE USER
    // =========================
    public String deleteUser() {

        User user = getCurrentUserEntity();

        userRepository.delete(user);

        return "User deleted successfully";
    }

    // =========================
    // MAPPER
    // =========================
    private UserResponse mapToResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .verified(user.isVerified())
                .build();
    }
}