package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Component
@Validated
@ConfigurationProperties("algashop.messaging.outbox")
public class OutboxProperties {

    @NotNull
    private Boolean enabled = false;

    @NotNull
    private Duration sendTimeout;

    @Min(1)
    @NotNull
    private Integer batchSize;

    @NotNull
    private Duration pollInterval;

    @Min(1)
    @NotNull
    private Integer maxAttempts;

    @NotNull
    private Duration backoff;

    @NotNull
    private Dispatcher dispatcher = new Dispatcher();

    @Data
    private static class Dispatcher {

        @NotNull
        private Boolean enabled = false;
    }
}
