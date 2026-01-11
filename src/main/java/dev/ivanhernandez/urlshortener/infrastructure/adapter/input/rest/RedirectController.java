package dev.ivanhernandez.urlshortener.infrastructure.adapter.input.rest;

import dev.ivanhernandez.urlshortener.application.dto.response.ErrorResponse;
import dev.ivanhernandez.urlshortener.application.port.input.GetOriginalUrlUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Tag(name = "Redirect", description = "Short URL redirection")
public class RedirectController {

    private final GetOriginalUrlUseCase getOriginalUrlUseCase;

    public RedirectController(GetOriginalUrlUseCase getOriginalUrlUseCase) {
        this.getOriginalUrlUseCase = getOriginalUrlUseCase;
    }

    @Operation(summary = "Redirect", description = "Redirects to the original URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect successful"),
            @ApiResponse(responseCode = "404", description = "URL not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "410", description = "URL expired",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(
            @Parameter(description = "Short code of the URL") @PathVariable String shortCode) {
        String originalUrl = getOriginalUrlUseCase.getOriginalUrl(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
