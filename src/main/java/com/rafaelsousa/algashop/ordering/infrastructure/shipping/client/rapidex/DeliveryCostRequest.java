package com.rafaelsousa.algashop.ordering.infrastructure.shipping.client.rapidex;

import lombok.Builder;

@Builder
public record DeliveryCostRequest(String originZipCode, String destinationZipCode) {
}