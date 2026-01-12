package dev.ivanhernandez.urlshortener.infrastructure.exception;

import dev.ivanhernandez.urlshortener.application.dto.response.ErrorResponse;
import dev.ivanhernandez.urlshortener.application.dto.response.ValidationErrorResponse;
import dev.ivanhernandez.urlshortener.domain.exception.ExpiredUrlException;
import dev.ivanhernandez.urlshortener.domain.exception.InvalidUrlException;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import dev.ivanhernandez.urlshortener.domain.exception.UrlOwnershipException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ExpiredUrlException.class)
    public ResponseEntity<ErrorResponse> handleExpiredUrlException(ExpiredUrlException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.GONE.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.GONE).body(error);
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UrlOwnershipException.class)
    public ResponseEntity<ErrorResponse> handleUrlOwnershipException(UrlOwnershipException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
