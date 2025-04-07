package com.example.familyplanner.service;

import com.example.familyplanner.Other.IPAdressUtil.IpAddressUtil;
import com.example.familyplanner.dto.requests.signInUp.RegistrationRequest;
import com.example.familyplanner.dto.requests.UpdateProfileRequest;
import com.example.familyplanner.dto.responses.UserResponseDto;
import com.example.familyplanner.entity.Role;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.UserRegistrationLogRepository;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.converter.UserConverter;
import com.example.familyplanner.service.exception.AlreadyExistException;
import com.example.familyplanner.service.exception.NotFoundException;
import com.example.familyplanner.service.exception.UserAlreadyExistException;
import com.example.familyplanner.service.validation.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter converter;

    @Mock
    private ValidationService validationService;

    @Mock
    private IpAddressUtil ipAddressUtil;

    @Mock
    private UserRegistrationLogRepository logRepository ;

    @InjectMocks
    private RegisterUserService registerUserService;

    private HttpServletRequest mockRequest;

    private RegistrationRequest registrationRequest;
    private User newUser;
    private UserResponseDto userResponseDto;
    private UpdateProfileRequest updateProfileRequest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockRequest = mock(HttpServletRequest.class);

        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(mockRequest.getRemoteAddr()).thenReturn("192.168.1.100");

        when(ipAddressUtil.getClientIp(mockRequest)).thenCallRealMethod();
        when(logRepository.countRecentRegistrations(anyString(), any(LocalDateTime.class))).thenReturn(0L);

        registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("testuser");
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("Password!123");
        registrationRequest.setAge(25);
        registrationRequest.setAvatarId("testAvatarId");

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setRoleName("USER");

        newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setUsername("testuser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("encodedPassword");
        newUser.setRole(role);
        newUser.setAvatarId("testAvatarId");
        newUser.setAge(25);

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(newUser.getId());
        userResponseDto.setUsername("testuser");
        userResponseDto.setEmail("test@example.com");
        userResponseDto.setRole(role);
        userResponseDto.setAvatarId("testAvatarId");
        userResponseDto.setAge(25);

        updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setUsername("updateduser");
        updateProfileRequest.setEmail("updated@example.com");
        updateProfileRequest.setAvatarId("avatar1");
        updateProfileRequest.setAge(30);
    }

    @Test
    void createNewUser_ShouldCreateUserWhenEmailDoesNotExist() {

        when(validationService.userExists(anyString())).thenReturn(false);
        when(converter.createUserFromDto(any(RegistrationRequest.class))).thenReturn(newUser);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(converter.createDtoFromUser(any(User.class))).thenReturn(userResponseDto);
//        when(mockRequest.getRemoteAddr()).thenReturn("192.168.1.100");
//        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195");


        UserResponseDto result = registerUserService.createNewUser(registrationRequest, mockRequest);

        assertNotNull(result);
        assertEquals(userResponseDto.getId(), result.getId());
        assertEquals(userResponseDto.getUsername(), result.getUsername());
        assertEquals(userResponseDto.getEmail(), result.getEmail());

        verify(ipAddressUtil).getClientIp(mockRequest);
        verify(logRepository).countRecentRegistrations(anyString(), any(LocalDateTime.class));

        verify(validationService).userExists("test@example.com");
        verify(converter).createUserFromDto(registrationRequest);
        verify(userRepository).save(newUser);
        verify(converter).createDtoFromUser(newUser);
    }

    @Test
    void createNewUser_ShouldThrowExceptionWhenEmailExists() {

        when(validationService.userExists(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () ->
                registerUserService.createNewUser(registrationRequest, mockRequest)
        );

        verify(validationService).userExists("test@example.com");
        verify(converter, never()).createUserFromDto(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserProfile_ShouldUpdateUserWhenUserExists() {

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(newUser));
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(converter.createDtoFromUser(any(User.class))).thenReturn(userResponseDto);

        UserResponseDto result = registerUserService.updateUserProfile("test@example.com", updateProfileRequest);

        assertNotNull(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(newUser);
        verify(converter).createDtoFromUser(newUser);
    }

    @Test
    void updateUserProfile_ShouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                registerUserService.updateUserProfile("nonexistent@example.com", updateProfileRequest)
        );

        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserProfile_ShouldThrowExceptionWhenNewEmailAlreadyExists() {

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(newUser));
        when(validationService.userExists("updated@example.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () ->
                registerUserService.updateUserProfile("test@example.com", updateProfileRequest)
        );

        verify(userRepository).findByEmail("test@example.com");
        verify(validationService).userExists("updated@example.com");
        verify(userRepository, never()).save(any());
    }
}