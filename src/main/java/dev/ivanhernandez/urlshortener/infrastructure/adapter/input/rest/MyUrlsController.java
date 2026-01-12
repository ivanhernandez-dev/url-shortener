package dev.ivanhernandez.urlshortener.infrastructure.adapter.input.rest;

import dev.ivanhernandez.urlshortener.application.dto.request.CreateUrlRequest;
import dev.ivanhernandez.urlshortener.application.dto.response.ErrorResponse;
import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;
import dev.ivanhernandez.urlshortener.application.dto.response.UrlStatsResponse;
import dev.ivanhernandez.urlshortener.application.dto.response.ValidationErrorResponse;
import dev.ivanhernandez.urlshortener.application.port.input.CreateUserUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.input.DeleteUserUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.input.GetUserUrlStatsUseCase;
import dev.ivanhernandez.urlshortener.application.port.input.GetUserUrlsUseCase;
import dev.ivanhernandez.urlshortener.infrastructure.config.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/my-urls")
@Tag(name = "My URLs", description = "Manage your own shortened URLs (requires authentication)")
@SecurityRequirement(name = "bearerAuth")
public class MyUrlsController {

    private final GetUserUrlsUseCase getUserUrlsUseCase;
    private final CreateUserUrlUseCase createUserUrlUseCase;
    private final DeleteUserUrlUseCase deleteUserUrlUseCase;
    private final GetUserUrlStatsUseCase getUserUrlStatsUseCase;

    public MyUrlsController(
            GetUserUrlsUseCase getUserUrlsUseCase,
            CreateUserUrlUseCase createUserUrlUseCase,
            DeleteUserUrlUseCase deleteUserUrlUseCase,
            GetUserUrlStatsUseCase getUserUrlStatsUseCase) {
        this.getUserUrlsUseCase = getUserUrlsUseCase;
        this.createUserUrlUseCase = createUserUrlUseCase;
        this.deleteUserUrlUseCase = deleteUserUrlUseCase;
        this.getUserUrlStatsUseCase = getUserUrlStatsUseCase;
    }

    @Operation(summary = "List my URLs", description = "Returns all URLs created by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URLs retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<List<ShortUrlResponse>> getMyUrls(
            @AuthenticationPrincipal AuthenticatedUser user) {
        List<ShortUrlResponse> urls = getUserUrlsUseCase.getUserUrls(user.userId());
        return ResponseEntity.ok(urls);
    }

    @Operation(summary = "Create URL", description = "Creates a new short URL associated with your account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "URL created successfully",
                    content = @Content(schema = @Schema(implementation = ShortUrlResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid URL or alias already exists",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping
    public ResponseEntity<ShortUrlResponse> createMyUrl(
            @Valid @RequestBody CreateUrlRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        ShortUrlResponse response = createUserUrlUseCase.createUserUrl(
                request, user.userId(), user.tenantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delete my URL", description = "Deletes one of your shortened URLs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "URL deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "URL not found or not owned by you",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteMyUrl(
            @Parameter(description = "Short code of the URL") @PathVariable String shortCode,
            @AuthenticationPrincipal AuthenticatedUser user) {
        deleteUserUrlUseCase.deleteUserUrl(shortCode, user.userId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get URL statistics", description = "Returns access statistics for one of your URLs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved",
                    content = @Content(schema = @Schema(implementation = UrlStatsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "URL not found or not owned by you",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponse> getMyUrlStats(
            @Parameter(description = "Short code of the URL") @PathVariable String shortCode,
            @AuthenticationPrincipal AuthenticatedUser user) {
        UrlStatsResponse response = getUserUrlStatsUseCase.getUserUrlStats(shortCode, user.userId());
        return ResponseEntity.ok(response);
    }
}
