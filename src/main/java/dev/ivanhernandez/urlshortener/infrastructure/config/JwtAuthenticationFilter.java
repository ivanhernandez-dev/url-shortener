package dev.ivanhernandez.urlshortener.infrastructure.config;

import dev.ivanhernandez.urlshortener.infrastructure.adapter.output.auth.AuthServiceClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthServiceClient authServiceClient;

    public JwtAuthenticationFilter(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            authServiceClient.introspect(token).ifPresent(introspectResponse -> {
                AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                        introspectResponse.userId(),
                        introspectResponse.tenantId()
                );

                List<SimpleGrantedAuthority> authorities = introspectResponse.roles() != null
                        ? introspectResponse.roles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .toList()
                        : List.of(new SimpleGrantedAuthority("ROLE_USER"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        token,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
