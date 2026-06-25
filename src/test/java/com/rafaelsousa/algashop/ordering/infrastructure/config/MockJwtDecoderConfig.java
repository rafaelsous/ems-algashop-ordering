package com.rafaelsousa.algashop.ordering.infrastructure.config;

import com.rafaelsousa.algashop.ordering.utils.MockJwtFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
public class MockJwtDecoderConfig {

	@Bean
	@Primary
	public JwtDecoder jwtDecoder() {
		return MockJwtFactory.createMockJwtDecoder();
	}
}