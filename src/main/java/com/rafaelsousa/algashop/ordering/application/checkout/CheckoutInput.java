package com.rafaelsousa.algashop.ordering.application.checkout;

import com.rafaelsousa.algashop.ordering.application.order.query.BillingData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutInput {
    private UUID shoppingCartId;
    private String paymentMethod;
    private ShippingInput shipping;
    private BillingData billing;
}