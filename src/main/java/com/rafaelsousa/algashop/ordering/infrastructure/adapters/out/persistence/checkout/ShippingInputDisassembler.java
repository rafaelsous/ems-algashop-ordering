package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.checkout;

import com.rafaelsousa.algashop.ordering.core.ports.commons.AddressData;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.*;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.Recipient;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.Shipping;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ShippingInput;
import org.springframework.stereotype.Component;

@Component
public class ShippingInputDisassembler {

    public Shipping toDomain(ShippingInput shippingInput,
                             CalculationResponse calculationResponse) {
        AddressData address = shippingInput.getAddress();
        return Shipping.builder()
                .cost(calculationResponse.cost())
                .expectedDate(calculationResponse.expectedDeliveryDate())
                .recipient(Recipient.builder()
                        .fullName(new FullName(
                                shippingInput.getRecipient().getFirstName(),
                                shippingInput.getRecipient().getLastName()))
                        .document(new Document(shippingInput.getRecipient().getDocument()))
                        .phone(new Phone(shippingInput.getRecipient().getPhone()))
                        .build())
                .address(Address.builder()
                        .street(address.getStreet())
                        .number(address.getNumber())
                        .complement(address.getComplement())
                        .neighborhood(address.getNeighborhood())
                        .city(address.getCity())
                        .state(address.getState())
                        .zipCode(new ZipCode(address.getZipCode()))
                        .build())
                .build();
    }
}