package com.example.familyplanner.dto;

import com.example.familyplanner.dto.requests.signInUp.RegistrationRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("JohnDoe");
        request.setEmail("john@example.com");
        request.setPassword("Password@1");
        request.setAge(25);
        request.setAvatarId("avatar1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

//    @Test
//    void testInvalidUsernameSpecialChars() {
//        RegistrationRequest request = new RegistrationRequest();
//        request.setUsername("John*Doe");
//        request.setEmail("john@example.com");
//        request.setPassword("Password@1");
//        request.setAge(25);
//        request.setAvatarId("avatar1");
//        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
//        assertEquals(1, violations.size());
//        assertTrue(violations.iterator().next().getMessage().contains("Username must not contain any special characters"));
//    }

    @Test
    void testInvalidUsernameTooShort() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("J");
        request.setEmail("john@example.com");
        request.setPassword("Password@1");
        request.setAge(25);
        request.setAvatarId("avatar1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Username should have at least 2 symbols"));
    }

    @Test
    void testInvalidUsernameTooLong() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("JohnDoeJohnDoeJohn");
        request.setEmail("john@example.com");
        request.setPassword("Password@1");
        request.setAge(25);
        request.setAvatarId("avatar1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Username can't be more than 15 symbols"));
    }

    @Test
    void testInvalidEmailFormat() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("JohnDoe");
        request.setEmail("invalid-email");
        request.setPassword("Password@1");
        request.setAge(25);
        request.setAvatarId("avatar1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Email must be a valid email address with a proper domain"));
    }

    @Test
    void testInvalidPasswordNoSpecialChar() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("JohnDoe");
        request.setEmail("john@example.com");
        request.setPassword("Password1");
        request.setAge(25);
        request.setAvatarId("avatar1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character"));
    }

    @Test
    void testInvalidPasswordTooShort() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("JohnDoe");
        request.setEmail("john@example.com");
        request.setPassword("Pp@1");
        request.setAge(25);
        request.setAvatarId("avatar1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Password should have at least 8 symbols"));
    }

    @Test
    void testInvalidPasswordTooLong() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("JohnDoe");
        request.setEmail("john@example.com");
        request.setPassword("P@sswordThatIsWayTooLong1234567");
        request.setAge(25);
        request.setAvatarId("avatar1");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Password can't be longer than 25 symbols"));
    }


}