package com.medicinska.rezervacija.apigateway.filter;

import com.medicinska.rezervacija.apigateway.security.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import java.util.Set;
import java.util.List;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password-request",
            "/auth/reset-password",
            "/auth/contact",
            "/auth/v3/api-docs",
            "/auth/swagger-ui",
            "/login",
            "/register",
            "/forgot-password-request",
            "/reset-password",
            "/contact",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Autowired
    private JwtService jwtService;

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            if (isPublicEndpoint(path) || request.getMethod().equals(HttpMethod.OPTIONS)) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            try {
                if (jwtService.isTokenExpired(token)) {
                    return unauthorizedResponse(exchange, "Token expired");
                }

                String username = jwtService.extractUsername(token);
                Set<String> roles = jwtService.extractRoles(token);

                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-Auth-Username", username)
                        .header("X-Auth-Roles", String.join(",", roles))
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                return unauthorizedResponse(exchange, "JWT validation failed: " + e.getMessage());
            }
        };
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String errorMessage) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("X-Auth-Error", errorMessage);
        return response.setComplete();
    }

    public static class Config {
    }
}