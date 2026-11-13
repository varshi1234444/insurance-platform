package com.ashith.insurance.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final SecretKey key;

    public JwtAuthGlobalFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Remove any identity headers a client may have sent, so they cannot be faked
        ServerHttpRequest cleaned = exchange.getRequest().mutate()
                .headers(h -> {
                    h.remove("X-User-Email");
                    h.remove("X-User-Role");
                }).build();
        ServerWebExchange cleanExchange = exchange.mutate().request(cleaned).build();

        String path = cleaned.getURI().getPath();
        if (path.startsWith("/api/auth/") || path.startsWith("/actuator/health")) {
            return chain.filter(cleanExchange);
        }

        String header = cleaned.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return unauthorized(cleanExchange);
        }

        try {
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(header.substring(7)).getPayload();
            ServerHttpRequest withUser = cleaned.mutate()
                    .header("X-User-Email", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();
            return chain.filter(cleanExchange.mutate().request(withUser).build());
        } catch (JwtException | IllegalArgumentException e) {
            return unauthorized(cleanExchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}