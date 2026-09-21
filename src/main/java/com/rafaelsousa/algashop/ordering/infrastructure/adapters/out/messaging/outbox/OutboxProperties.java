package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("algashop.messaging.outbox")
public class OutboxProperties {
    private Boolean enabled = false;
    private Dispatcher dispatcher = new Dispatcher();

    @Data
    private static class Dispatcher {
        private Boolean enabled = false;
    }
}
