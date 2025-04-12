package com.example.familyplanner.dto.requests.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to complete password reset")
public class ResetPasswordRequest {
    @NotBlank(message = "Token cannot be empty")
    @Size(min = 6, max = 6, message = "Token must be 6 digits")
    @Pattern(regexp = "^[0-9]{6}$", message = "Token must contain only digits")
    @Schema(description = "6-digit password reset code received via email", example = "123456", required = true)
    private String token;

    @NotBlank(message = "New password cannot be empty")
    @Pattern(
            regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    @Size(min = 8, message = "Password should have at least 8 symbols")
    @Size(max = 25, message = "Password can't be bigger than 25 symbols")
    @Schema(description = "New password", example = "NewPassword!23", required = true)
    private String newPassword;

    @NotBlank(message = "Password confirmation cannot be empty")
    @Schema(description = "Confirmation of new password", example = "NewPassword!23", required = true)
    private String confirmPassword;

    @NotBlank(message = "Email cannot be empty")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email must be a valid email address with a proper domain")
    @Size(max = 40, message = "Email cannot be longer than 40 characters")
    @Schema(description = "User's email address", example = "blackjack@ua", required = true)
    private String email;
}