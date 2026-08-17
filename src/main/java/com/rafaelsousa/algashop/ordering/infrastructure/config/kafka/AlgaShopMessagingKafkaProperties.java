package com.rafaelsousa.algashop.ordering.infrastructure.config.kafka;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties("algashop.messaging.kafka")
public class AlgaShopMessagingKafkaProperties {

    @NotBlank
    private String productEventTopicName;
}
