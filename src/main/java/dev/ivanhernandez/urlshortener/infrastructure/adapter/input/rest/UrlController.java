package dev.ivanhernandez.urlshortener.infrastructure.adapter.input.rest;

import dev.ivanhernandez.urlshortener.application.dto.request.CreateUrlRequest;
import dev.ivanhernandez.urlshortener.application.dto.response.ErrorResponse;
import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;
import dev.ivanhernandez.urlshortener.application.dto.response.ValidationErrorResponse;
import dev.ivanhernandez.urlshortener.application.port.input.CreateShortUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.input.DeleteUrlUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
@Tag(name = "URL Shortener", description = "Public endpoint for creating shortened URLs")
public class UrlController {

    private final CreateShortUrlUseCase createShortUrlUseCase;
    private final DeleteUrlUseCase deleteUrlUseCase;

    public UrlController(CreateShortUrlUseCase createShortUrlUseCase, DeleteUrlUseCase deleteUrlUseCase) {
        this.createShortUrlUseCase = createShortUrlUseCase;
        this.deleteUrlUseCase = deleteUrlUseCase;
    }

    @Operation(summary = "Create short URL", description = "Generates a short code for a long URL (anonymous)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "URL created successfully",
                    content = @Content(schema = @Schema(implementation = ShortUrlResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid URL or alias already exists",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ShortUrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        ShortUrlResponse response = createShortUrlUseCase.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delete anonymous URL", description = "Deletes a publicly created shortened URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "URL deleted"),
            @ApiResponse(responseCode = "404", description = "URL not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(
            @Parameter(description = "Short code of the URL") @PathVariable String shortCode) {
        deleteUrlUseCase.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}
