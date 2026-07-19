package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.product.client.http;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class ProductCatalogApiConfig {

    @Bean
    public ProductCatalogApiClient productCatalogApiClient(
        @LoadBalanced RestClient.Builder builder,
        ProductCatalogIntegrationProperties properties,
        @Qualifier("productCatalogApiClientInterceptor") OAuth2ClientHttpRequestInterceptor interceptor
    ) {
        RestClient restClient = builder.baseUrl(properties.getUrl())
                    .requestFactory(generateClientHttpRequestFactory())
                    .requestInterceptor(interceptor)
                    .build();
        
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

        return proxyFactory.createClient(ProductCatalogApiClient.class);
    }

    @Bean("productCatalogApiClientInterceptor")
    public OAuth2ClientHttpRequestInterceptor productCatalogApiClientInterceptor(
        ProductCatalogIntegrationProperties properties, OAuth2AuthorizedClientManager manager
    ) {
        OAuth2ClientHttpRequestInterceptor interceptor = new OAuth2ClientHttpRequestInterceptor(manager);
        interceptor.setClientRegistrationIdResolver(_ -> properties.getOauth2ClientRegistrationId());
        interceptor.setPrincipalResolver(_ -> generatePrincipal(properties.getOauth2ClientRegistrationId()));

        return interceptor;
    }

    private ClientHttpRequestFactory generateClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(5));
        factory.setConnectTimeout(Duration.ofSeconds(2));

        return factory;
    }

    private Authentication generatePrincipal(String principalName) {
        return new AbstractAuthenticationToken(Collections.emptySet()) {
            @Override
            public Object getPrincipal() {
                return principalName;
            }

            @Override
            public Object getCredentials() {
                return null;
            }
        };
    }
}