package com.rafaelsousa.algashop.ordering.core.application.checkout;

import com.rafaelsousa.algashop.ordering.core.ports.commons.AddressData;
import com.rafaelsousa.algashop.ordering.core.application.order.query.RecipientData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingInput {

    @NotNull @Valid
    private RecipientData recipient;

    @NotNull @Valid
    private AddressData address;
}