package com.rafaelsousa.algashop.ordering.application.checkout;

import com.rafaelsousa.algashop.ordering.application.order.query.BillingData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyNowInput {
    private UUID customerId;
    private UUID productId;
    private Integer quantity;
    private BillingData billing;
    private ShippingInput shipping;
    private String paymentMethod;
}