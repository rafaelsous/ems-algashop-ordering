package com.rafaelsousa.algashop.ordering.infrastructure.config.security.token;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
public class JwtAuthenticationConverterConfig {

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter(JwtGrantedAuthoritiesDelegatingConverter scopeAuthoritiesConverter) {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(scopeAuthoritiesConverter);

		return jwtAuthenticationConverter;
	}
}