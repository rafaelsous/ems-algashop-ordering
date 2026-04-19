package com.rafaelsousa.algashop.ordering.utils;

import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class MockJwtDecoderFactory {
    public static final String DEFAULT_SUBJECT = "user-test";
    public static final String DEFAULT_TOKEN_VALUE = "fake.jwt.token";
    public static final String NO_SCOPE_TOKEN_VALUE = "fake.jwt.no-scope";
    public static final String EXPIRED_TOKEN_VALUE = "fake.jwt.expired";
    public static final String DEFAULT_ISSUER_URI = "http://algashop-authorization-server:8081";
    public static final String[] DEFAULT_SCOPES =
            new String[] {
                "orders:read",
                "orders:write",
                "customers:read",
                "customers:write",
                "shopping-carts:read",
                "shopping-carts:write"
            };

    public static JwtDecoder createMockJwtDecoder() {
        JwtDecoder jwtDecoder = Mockito.mock(JwtDecoder.class);

        when(jwtDecoder.decode(DEFAULT_TOKEN_VALUE)).thenReturn(buildDefaultJwt());

        when(jwtDecoder.decode(NO_SCOPE_TOKEN_VALUE)).thenReturn(buildNoScopeJwt());

        when(jwtDecoder.decode(EXPIRED_TOKEN_VALUE))
                .thenThrow(new JwtException("Token is expired"));

        return jwtDecoder;
    }

    public static Jwt buildJwt(String tokenValue, String subject, String issuer, String[] scopes) {
        Instant now = Instant.now();
        Instant expires = now.plusSeconds(600);

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", subject);
        claims.put("iss", issuer);
        claims.put("exp", expires);

        if (scopes != null && scopes.length > 0) {
            claims.put("scope", String.join(" ", scopes));
        }

        return Jwt.withTokenValue(tokenValue)
                .issuedAt(now)
                .expiresAt(expires)
                .issuer(issuer)
                .subject(subject)
                .claims(c -> c.putAll(claims))
                .headers(h -> h.put("alg", "none"))
                .build();
    }

    public static Jwt buildDefaultJwt() {
        return buildJwt(DEFAULT_TOKEN_VALUE, DEFAULT_SUBJECT, DEFAULT_ISSUER_URI, DEFAULT_SCOPES);
    }

    public static Jwt buildNoScopeJwt() {
        return buildJwt(NO_SCOPE_TOKEN_VALUE, DEFAULT_SUBJECT, DEFAULT_ISSUER_URI, new String[]{});
    }
}