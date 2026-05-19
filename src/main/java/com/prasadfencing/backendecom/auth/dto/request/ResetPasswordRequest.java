package com.prasadfencing.backendecom.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;

    @Size(min = 6,
            message = "Password must be at least 6 characters")
    private String newPassword;
}