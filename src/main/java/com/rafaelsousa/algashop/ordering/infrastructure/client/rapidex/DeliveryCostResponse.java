package com.rafaelsousa.algashop.ordering.infrastructure.client.rapidex;

import lombok.Builder;

@Builder
public record DeliveryCostResponse(String deliveryCost, Long estimatedDaysToDeliver) {
}