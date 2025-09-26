package com.jasondt.apigateway.filter;

import com.jasondt.apigateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    public AuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final List<Pattern> PUBLIC_PATTERNS = List.of(
            Pattern.compile("^/api/auth/login$"),
            Pattern.compile("^/api/auth/register$"),
            Pattern.compile("^/swagger-ui.*"),
            Pattern.compile("^/v3/api-docs.*"),
            Pattern.compile("^/api/public.*"),
            Pattern.compile("^/api/search$"),
            Pattern.compile("^/api/history/top-tracks$"),
            Pattern.compile("^/api/history/top-artists$"),
            Pattern.compile("^/api/history/top-albums$"),
            Pattern.compile("^/api/history/tracks/[a-fA-F0-9\\-]+/plays$"),
            Pattern.compile("^/api/history/users/[a-fA-F0-9\\-]+/top-tracks$"),
            Pattern.compile("^/api/history/users/[a-fA-F0-9\\-]+/top-artists$"),
            Pattern.compile("^/api/artists/[a-fA-F0-9\\-]+$"),
            Pattern.compile("^/api/artists/[a-fA-F0-9\\-]+/details$"),
            Pattern.compile("^/api/albums/[a-fA-F0-9\\-]+$"),
            Pattern.compile("^/api/tracks/[a-fA-F0-9\\-]+/with-plays$"),
            Pattern.compile("^/api/playlists/[a-fA-F0-9\\-]+$"),
            Pattern.compile("^/api/playlists/users/[a-fA-F0-9\\-]+$"),
            Pattern.compile("^/api/users/[a-fA-F0-9\\-]+$")
    );
    private boolean isPublicRoute(String path) {
        return PUBLIC_PATTERNS.stream().anyMatch(p -> p.matcher(path).matches());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Claims claims = jwtUtil.extractAllClaims(token);
        String userId = claims.get("userId", String.class);

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-User-Id", userId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());

    }

    @Override
    public int getOrder() {
        return -1;
    }
}