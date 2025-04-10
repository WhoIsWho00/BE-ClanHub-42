package com.example.familyplanner.service;

import com.example.familyplanner.entity.PasswordResetToken;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.PasswordResetTokenRepository;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.exception.ExpiredTokenException;
import com.example.familyplanner.service.exception.InvalidTokenException;
import com.example.familyplanner.service.exception.NotFoundException;
import com.example.familyplanner.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Token expiration time in minutes - 5 minutes as per requirements
    private static final int TOKEN_EXPIRATION_MINUTES = 5;


    public void sendResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));

        // Generate 6-digit numeric code as per requirements
        String token = generateSixDigitCode();

        // Invalidate any existing tokens for this user
        List<PasswordResetToken> existingTokens = resetTokenRepository.findByUser(user);
        existingTokens.forEach(t -> {
            t.setExpiryDate(LocalDateTime.now().minusMinutes(1));
            resetTokenRepository.save(t);
        });

        // Create new token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES));
        resetToken.setUser(user);
        resetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(String token, String newPassword, String confirmPassword, String email) {
        // Validate that passwords match
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match");
        }

        // Find the token
        PasswordResetToken resetToken = resetTokenRepository.findByTokenAndUserEmail(token, email)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

//        if(!resetToken.getToken().equals(token)) {
//            throw new InvalidTokenException("Invalid token");
//        }

        // Check if token is expired
        if (resetToken.isExpired()) {
            throw new ExpiredTokenException("Token has expired");
        }

        // Update user's password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used by expiring it
        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        resetTokenRepository.save(resetToken);
    }
    private String generateSixDigitCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
