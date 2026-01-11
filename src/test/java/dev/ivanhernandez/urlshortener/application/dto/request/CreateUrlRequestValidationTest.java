package dev.ivanhernandez.urlshortener.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CreateUrlRequest Validation")
class CreateUrlRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("valid request should have no violations")
    void validRequest_shouldHaveNoViolations() {
        CreateUrlRequest request = new CreateUrlRequest(
                "https://example.com",
                "myalias",
                LocalDateTime.now().plusDays(30)
        );

        Set<ConstraintViolation<CreateUrlRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @DisplayName("blank originalUrl should produce violation")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void blankOriginalUrl_shouldProduceViolation(String originalUrl) {
        CreateUrlRequest request = new CreateUrlRequest(originalUrl, null, null);

        Set<ConstraintViolation<CreateUrlRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("originalUrl")));
    }

    @ParameterizedTest
    @DisplayName("invalid URL format should produce violation")
    @ValueSource(strings = {
            "not-a-url",
            "just-text",
            "www.missing-protocol.com",
            "://missing-scheme.com",
            "htp://typo-protocol.com"
    })
    void invalidUrlFormat_shouldProduceViolation(String invalidUrl) {
        CreateUrlRequest request = new CreateUrlRequest(invalidUrl, null, null);

        Set<ConstraintViolation<CreateUrlRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @DisplayName("valid URL formats should pass validation")
    @ValueSource(strings = {
            "https://example.com",
            "http://localhost:8080",
            "https://www.google.com/search?q=test",
            "http://192.168.1.1:3000/api"
    })
    void validUrlFormats_shouldPassValidation(String validUrl) {
        CreateUrlRequest request = new CreateUrlRequest(validUrl, null, null);

        Set<ConstraintViolation<CreateUrlRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("null customAlias should be valid")
    void nullCustomAlias_shouldBeValid() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, null);

        Set<ConstraintViolation<CreateUrlRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("null expiresAt should be valid")
    void nullExpiresAt_shouldBeValid() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", "alias", null);

        Set<ConstraintViolation<CreateUrlRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}
