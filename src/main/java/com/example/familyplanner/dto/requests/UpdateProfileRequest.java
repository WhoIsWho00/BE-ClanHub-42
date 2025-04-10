package com.example.familyplanner.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request model for updating user profile")
public class UpdateProfileRequest {
    @Size(min = 2, message = "Username should have at least 2 symbols")
    @Size(max = 15, message = "Username can't be more than 15 symbols")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must not contain any special characters")
    @Schema(description = "User's username", example = "JohnDoe")
    private String username;

    @NotBlank(message = "Email cannot be empty")
//    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email must be a valid email address with a proper domain")
    @Schema(description = "User's email address", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "User's chosen avatar identifier", example = "avatar1")
    private String avatarId;

    @Schema(description = "User's age", example = "25", minimum = "5", maximum = "100")
    private Integer age;
}