package com.rafaelsousa.algashop.ordering.utils;

import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockJwtFactory {
    public static final String DEFAULT_SUBJECT = "6e148bd5-47f6-4022-b9da-07cfaa294f7a";
    public static final String ALT_CUSTOMER_SUBJECT = "019f18ee-f840-728c-889f-aa1a6e5dc8cd";
    public static final String DEFAULT_TOKEN_VALUE = "fake.jwt.token";
    public static final String MANAGER_TOKEN_VALUE = "fake.jwt.manager";
    public static final String ALT_TOKEN_VALUE = "fake.jwt.alt-customer";
    public static final String NO_SCOPE_TOKEN_VALUE = "fake.jwt.no-scope";
    public static final String EXPIRED_TOKEN_VALUE = "fake.jwt.expired";
    public static final String DEFAULT_ISSUER_URI = "http://auth.algashop.local:8081";
    public static final String DEFAULT_ROLE = "CUSTOMER";
    public static final String MANAGER_ROLE = "MANAGER";
    public static final String[] DEFAULT_AUDIENCES = new String[] { "ecommerce-web-app" };
    public static final String[] DEFAULT_SCOPES = new String[] {
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
        
        when(jwtDecoder.decode(MANAGER_TOKEN_VALUE)).thenReturn(buildManagerJwt());

        when(jwtDecoder.decode(NO_SCOPE_TOKEN_VALUE)).thenReturn(buildNoScopeJwt());
        
        when(jwtDecoder.decode(ALT_TOKEN_VALUE)).thenReturn(buildAltCustomerJwt());

        when(jwtDecoder.decode(EXPIRED_TOKEN_VALUE))
                .thenThrow(new JwtException("Token is expired"));

        return jwtDecoder;
    }

    public static Jwt buildJwt(String tokenValue, String subject, String issuer, String[] scopes,
                               String role, String[] audiences) {
        Instant now = Instant.now();
        Instant expires = now.plusSeconds(600);

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", subject);
        claims.put("iss", issuer);
        claims.put("role", role);
        claims.put("aud", List.of(audiences));
        claims.put("scope", List.of(scopes));
        claims.put("exp", expires);

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
        return buildJwt(DEFAULT_TOKEN_VALUE, DEFAULT_SUBJECT, DEFAULT_ISSUER_URI, DEFAULT_SCOPES,
            DEFAULT_ROLE, DEFAULT_AUDIENCES);
    }
    
    public static Jwt buildManagerJwt() {
        return buildJwt(MANAGER_TOKEN_VALUE, DEFAULT_SUBJECT, DEFAULT_ISSUER_URI, DEFAULT_SCOPES,
            MANAGER_ROLE, DEFAULT_AUDIENCES);
    }
    
    public static Jwt buildAltCustomerJwt() {
        return buildJwt(ALT_TOKEN_VALUE, ALT_CUSTOMER_SUBJECT, DEFAULT_ISSUER_URI, DEFAULT_SCOPES,
            DEFAULT_ROLE, DEFAULT_AUDIENCES);
    }

    public static Jwt buildNoScopeJwt() {
        return buildJwt(NO_SCOPE_TOKEN_VALUE, DEFAULT_SUBJECT, DEFAULT_ISSUER_URI, new String[]{},
            DEFAULT_ROLE, DEFAULT_AUDIENCES);
    }
}