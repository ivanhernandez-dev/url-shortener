package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Component
public class AuthServiceClient {

    private final RestClient restClient;

    public AuthServiceClient(@Value("${auth-service.base-url}") String authServiceBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(authServiceBaseUrl)
                .build();
    }

    public Optional<IntrospectResponse> introspect(String token) {
        try {
            IntrospectResponse response = restClient.post()
                    .uri("/api/v1/auth/introspect")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("token", token))
                    .retrieve()
                    .body(IntrospectResponse.class);

            if (response != null && response.active()) {
                return Optional.of(response);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
