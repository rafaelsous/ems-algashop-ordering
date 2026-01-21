package com.rafaelsousa.algashop.ordering.core.ports.in.checkout;

import com.rafaelsousa.algashop.ordering.core.ports.in.order.BillingData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID productId;

    @NotNull @Positive
    private Integer quantity;

    @NotNull @Valid
    private BillingData billing;

    @NotNull @Valid
    private ShippingInput shipping;

    @NotBlank
    private String paymentMethod;

    private UUID creditCardId;
}