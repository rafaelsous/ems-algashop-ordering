package com.rafaelsousa.algashop.ordering.core.application.checkout;

import com.rafaelsousa.algashop.ordering.core.ports.commons.AddressData;
import com.rafaelsousa.algashop.ordering.core.application.order.query.BillingData;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.*;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.Billing;
import org.springframework.stereotype.Component;

@Component
class BillingInputDisassembler {

    public Billing toDomain(BillingData billingData) {
        AddressData address = billingData.getAddress();
        return Billing.builder()
                .fullName(FullName.of(billingData.getFirstName(), billingData.getLastName()))
                .document(Document.of(billingData.getDocument()))
                .phone(Phone.of(billingData.getPhone()))
                .email(Email.of(billingData.getEmail()))
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