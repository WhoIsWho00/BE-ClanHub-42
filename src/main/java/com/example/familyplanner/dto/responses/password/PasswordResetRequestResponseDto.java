package com.example.familyplanner.dto.responses.password;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response after password reset request")
public class PasswordResetRequestResponseDto {
    @Schema(description = "Status message", example = "If your email is registered, a password reset code has been sent.")
    private String message;

    @Schema(description = "Whether the request was processed successfully", example = "true")
    private boolean success;
}