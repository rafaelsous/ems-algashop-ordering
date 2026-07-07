package com.rafaelsousa.algashop.ordering.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration
public class TestcontainerPostgreSQLConfig {

    static PostgreSQLContainer postgreSQLContainer
            = new PostgreSQLContainer("postgres:17-alpine");

    @Bean
    @ServiceConnection
    public PostgreSQLContainer postgreSQLContainer() {
        return postgreSQLContainer;
    }
}