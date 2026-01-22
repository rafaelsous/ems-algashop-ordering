package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.shipping.client.rapidex;

import lombok.Builder;

@Builder
public record DeliveryCostRequest(String originZipCode, String destinationZipCode) {
}