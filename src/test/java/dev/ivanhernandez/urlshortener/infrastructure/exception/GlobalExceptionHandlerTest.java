package dev.ivanhernandez.urlshortener.infrastructure.exception;

import dev.ivanhernandez.urlshortener.application.dto.response.ErrorResponse;
import dev.ivanhernandez.urlshortener.application.dto.response.ValidationErrorResponse;
import dev.ivanhernandez.urlshortener.domain.exception.ExpiredUrlException;
import dev.ivanhernandez.urlshortener.domain.exception.InvalidUrlException;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import dev.ivanhernandez.urlshortener.domain.exception.UrlOwnershipException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Mock
    private MethodArgumentNotValidException mockValidationException;

    @Mock
    private BindingResult mockBindingResult;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleUrlNotFoundException should return 404 with error message")
    void handleUrlNotFoundException_shouldReturn404WithMessage() {
        UrlNotFoundException exception = new UrlNotFoundException("abc123");

        ResponseEntity<ErrorResponse> response = handler.handleUrlNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertTrue(response.getBody().message().contains("abc123"));
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("handleExpiredUrlException should return 410 with error message")
    void handleExpiredUrlException_shouldReturn410WithMessage() {
        ExpiredUrlException exception = new ExpiredUrlException("expired123");

        ResponseEntity<ErrorResponse> response = handler.handleExpiredUrlException(exception);

        assertEquals(HttpStatus.GONE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(410, response.getBody().status());
        assertTrue(response.getBody().message().contains("expired123"));
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("handleInvalidUrlException should return 400 with error message")
    void handleInvalidUrlException_shouldReturn400WithMessage() {
        InvalidUrlException exception = new InvalidUrlException("Custom alias already exists");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidUrlException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Custom alias already exists", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("handleValidationExceptions should return 400 with field errors")
    void handleValidationExceptions_shouldReturn400WithFieldErrors() {
        FieldError fieldError1 = new FieldError("request", "originalUrl", "Invalid URL format");
        FieldError fieldError2 = new FieldError("request", "customAlias", "Alias too long");

        when(mockValidationException.getBindingResult()).thenReturn(mockBindingResult);
        when(mockBindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ValidationErrorResponse> response = handler.handleValidationExceptions(mockValidationException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Validation failed", response.getBody().message());
        assertEquals(2, response.getBody().errors().size());
        assertEquals("Invalid URL format", response.getBody().errors().get("originalUrl"));
        assertEquals("Alias too long", response.getBody().errors().get("customAlias"));
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("handleValidationExceptions should handle single field error")
    void handleValidationExceptions_shouldHandleSingleFieldError() {
        FieldError fieldError = new FieldError("request", "originalUrl", "Original URL is required");

        when(mockValidationException.getBindingResult()).thenReturn(mockBindingResult);
        when(mockBindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ValidationErrorResponse> response = handler.handleValidationExceptions(mockValidationException);

        assertEquals(1, response.getBody().errors().size());
        assertEquals("Original URL is required", response.getBody().errors().get("originalUrl"));
    }

    @Test
    @DisplayName("handleUrlOwnershipException should return 403 with error message")
    void handleUrlOwnershipException_shouldReturn403WithMessage() {
        UrlOwnershipException exception = new UrlOwnershipException("owned123");

        ResponseEntity<ErrorResponse> response = handler.handleUrlOwnershipException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().status());
        assertTrue(response.getBody().message().contains("owned123"));
        assertNotNull(response.getBody().timestamp());
    }
}
