package com.example.familyplanner.service;

import com.example.familyplanner.dto.responses.UserResponseDto;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.converter.UserConverter;
import com.example.familyplanner.service.exception.NonExistingEmailException;
import com.example.familyplanner.service.exception.NotFoundException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindUserService {
    private final UserRepository userRepository;
    private final UserConverter converter;


    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(manager -> converter.createDtoFromUser(manager))
                .toList();
    }

    public UserResponseDto findUserByEmail(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            UserResponseDto response = converter.createDtoFromUser(userOptional.get());
            return response;
        } else {
            throw new NotFoundException("User with email " + email + " not found");
        }
    }

    public List<User> findAllFullDetails() {
        return userRepository.findAll();
    }

    public boolean existsByEmail(String email) {
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            return false;
        }
    }


}