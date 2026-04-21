package com.rafaelsousa.algashop.ordering.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class OAuth2ClientConfig {

	@Bean
	public OAuth2AuthorizedClientManager auth2AuthorizedClientManager(
		OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
		ClientRegistrationRepository clientRegistrationRepository
	) {
		OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
			.clientCredentials()
			.build();

		AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
			new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService);

		manager.setAuthorizedClientProvider(provider);

		return manager;
	}
}