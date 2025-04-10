package com.example.familyplanner.repository;


import com.example.familyplanner.entity.PasswordResetToken;
import com.example.familyplanner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    List<PasswordResetToken> findByUser(User user);

    Optional<PasswordResetToken> findByTokenAndUserEmail(String token, String email);
}
