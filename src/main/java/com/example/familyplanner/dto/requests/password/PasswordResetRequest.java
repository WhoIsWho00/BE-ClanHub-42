package com.example.familyplanner.dto.requests.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request to initiate password reset")
public class PasswordResetRequest {

    @NotBlank(message = "Email cannot be empty")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email must be a valid email address with a proper domain")
    @Size(max = 40, message = "Email cannot be longer than 40 characters")
    @Schema(description = "User's email address", example = "blackjack@ua", required = true)
    private String email;
}