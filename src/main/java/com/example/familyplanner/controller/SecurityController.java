package com.example.familyplanner.controller;

import com.example.familyplanner.Security.JWT.JwtCore;
import com.example.familyplanner.dto.requests.signInUp.LoginRequest;
import com.example.familyplanner.dto.requests.password.PasswordResetRequest;
import com.example.familyplanner.dto.requests.signInUp.RegistrationRequest;
import com.example.familyplanner.dto.requests.password.ResetPasswordRequest;
import com.example.familyplanner.dto.responses.*;
import com.example.familyplanner.dto.responses.password.PasswordResetRequestResponseDto;
import com.example.familyplanner.dto.responses.password.PasswordResetResponseDto;
import com.example.familyplanner.dto.responses.signInUp.AuthResponseDto;
import com.example.familyplanner.dto.responses.signInUp.RegisterResponseDto;
import com.example.familyplanner.service.FindUserService;
import com.example.familyplanner.service.PasswordResetService;
import com.example.familyplanner.service.RegisterUserService;
import com.example.familyplanner.service.exception.NonExistingEmailException;
import com.example.familyplanner.service.exception.NotFoundException;
import com.example.familyplanner.service.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for user authentication and password recovery")
public class SecurityController {


    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    private final RegisterUserService registerUserService;
    private final FindUserService findUserService;
    private final PasswordResetService passwordResetService;


    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user and returns JWT token along with user info",
            responses = {@ApiResponse(responseCode = "200", description = "Successful login",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = """
                                    {
                                      "token": "jwt-token-string",
                                      "user": {
                                        "id": 1,
                                        "username": "john_doe",
                                        "email": "john@example.com"
                                      },
                                      "email": "john@example.com",
                                      "message": "Login successful"
                                    }
                                    """
                    ))),
                    @ApiResponse(responseCode = "400", description = "Invalid request (missing email or password)",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Missing email or password.",
                                              "path": "/api/auth/sign-in"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Invalid email or password.",
                                              "path": "/api/auth/sign-in"
                                            }
                                            """
                            ))),

                    @ApiResponse(responseCode = "404", description = "Page not found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Page not found.",
                                              "path": "/api/auth/sign-in"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred.",
                                              "path": "/api/auth/sign-in"
                                            }
                                            """
                            )))
                    }
            )
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponseDto> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Спочатку перевіряємо, чи існує користувач
            boolean userExists = findUserService.existsByEmail(loginRequest.getEmail());

            if (!userExists) {
                throw new NonExistingEmailException("Email not found");
            }

            // Тільки якщо користувач існує, намагаємося аутентифікувати
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwt = jwtCore.createToken(loginRequest.getEmail());
                UserResponseDto userDto = findUserService.findUserByEmail(loginRequest.getEmail());

                AuthResponseDto responseDto = AuthResponseDto.builder()
                        .token(jwt)
                        .user(userDto)
                        .email(loginRequest.getEmail())
                        .message("Login successful")
                        .build();

                return ResponseEntity.ok(responseDto);
            } catch (AuthenticationException e) {
                // Викидаємо ValidationException для неправильного пароля
                throw new ValidationException("Invalid email or password");
            }
        } catch (NonExistingEmailException | ValidationException e) {

            throw e;
        } catch (Exception e) {

            throw new NullPointerException("An unexpected error occurred: " + e.getMessage());
        }
    }


    @Operation(
            summary = "Register new user",
            description = "Registers a new user and returns JWT token along with user info",
            responses = {@ApiResponse(responseCode = "201", description = "User successfully registered",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = """
                                    {
                                      "user": {
                                        "id": 2,
                                        "username": "new_user",
                                        "email": "new_user@example.com"
                                      },
                                      "token": "jwt-token-string",
                                      "message": "User successfully registered",
                                      "status": "success"
                                    }
                                    """
                    ))),
                    @ApiResponse(responseCode = "400", description = "User already exists or validation failed",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": {
                                                "email": "User with this email already exists or email is invalid.",
                                                "username": "Username already taken or invalid."
                                              },
                                              "path": "/api/auth/sign-up"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (access denied)",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "Access denied. Insufficient permissions.",
                                              "path": "/api/auth/sign-up"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "409", description = "Data validation conflict",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "email already exists or email is invalid.",
                                              "path": "/api/auth/sign-up"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "429", description = "Too many request",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2025-03-25T16:26:19.597Z",
                                                "status": 429,
                                                "error": "Too many requests",
                                                "message": "Too many requests at the same time",
                                                "path": "/api/auth/sign-up"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred.",
                                              "path": "/api/auth/sign-up"
                                            }
                                            """
                            )))
            }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<RegisterResponseDto> registerUser(@Valid @RequestBody RegistrationRequest request, HttpServletRequest httpRequest) {

        UserResponseDto newUser = registerUserService.createNewUser(request, httpRequest);

        String jwt = jwtCore.createToken(request.getEmail());

        RegisterResponseDto responseDto = RegisterResponseDto.builder()
                .user(newUser)
                .token(jwt)
                .message("User successfully registered")
                .status("success")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request password reset",
            description = "Sends a 6-digit code to the user's email for password reset",
            responses = {@ApiResponse(responseCode = "200", description = "Password reset request processed",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = """
                                    {
                                      "message": "If your email is registered, a password reset code has been sent."
                                    }
                                    """
                    ))),
                    @ApiResponse(responseCode = "400", description = "Invalid email format",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Invalid email format.",
                                              "path": "/api/auth/forgot-password"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (access denied)",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "Access denied. You do not have permission to perform this action.",
                                              "path": "/api/auth/forgot-password"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred.",
                                              "path": "/api/auth/forgot-password"
                                            }
                                            """
                            )))
            }
    )
//    public ResponseEntity<PasswordResetRequestResponseDto> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
//        try {
//            passwordResetService.sendResetToken(request.getEmail());
//        } catch (NotFoundException e) {
//            // Не показывает, созданна почта или нет(для коонфидециальности)
//        }
//
//        PasswordResetRequestResponseDto responseDto = PasswordResetRequestResponseDto.builder()
//                .message("If your email is registered, a password reset code has been sent.")
//                .build();
//
//        return ResponseEntity.ok(responseDto);
//    }
    public ResponseEntity<PasswordResetRequestResponseDto> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        boolean userExists = false;

        try {
            // Перевіряємо, чи існує користувач, не розкриваючи цю інформацію клієнту
            userExists = findUserService.existsByEmail(request.getEmail());

            // Відправляємо код скидання тільки якщо користувач існує
            if (userExists) {
                passwordResetService.sendResetToken(request.getEmail());
            }

            // В будь-якому випадку показуємо однакове повідомлення
            PasswordResetRequestResponseDto responseDto = PasswordResetRequestResponseDto.builder()
                    .message("If your email is registered, a password reset code has been sent.")
                    .success(true)
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            // Відправляємо загальне повідомлення про помилку
            PasswordResetRequestResponseDto errorResponse = PasswordResetRequestResponseDto.builder()
                    .message("This email is not registered in our system.")
                    .success(false)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password",
            description = "Resets password using the provided token and new password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successfully",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Your password has been reset successfully"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Invalid token or password mismatch",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Invalid or expired reset token.",
                                              "path": "/api/auth/reset-password"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (token does not belong to user)",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "Reset token is not associated with your account.",
                                              "path": "/api/auth/reset-password"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred.",
                                              "path": "/api/auth/reset-password"
                                            }
                                            """
                            )))}
    )
    public ResponseEntity<PasswordResetResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        //метод сам проверает не нарушены ли данные. В случае чего - сам прокинет exception
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());

        PasswordResetResponseDto responseDto = PasswordResetResponseDto.builder()
                .message("Your password has been reset successfully")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<String> handleInvalidAuthRequest() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("The endpoint you are trying to reach does not exist.");
    }
}