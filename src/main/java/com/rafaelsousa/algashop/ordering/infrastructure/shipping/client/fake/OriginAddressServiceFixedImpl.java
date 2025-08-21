package com.rafaelsousa.algashop.ordering.infrastructure.shipping.client.fake;

import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Address;
import com.rafaelsousa.algashop.ordering.domain.model.commons.ZipCode;
import org.springframework.stereotype.Component;

@Component
public class OriginAddressServiceFixedImpl implements OriginAddressService {

    @Override
    public Address originAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .number("1122")
                .neighborhood("North Ville")
                .city("York")
                .state("South California")
                .zipCode(ZipCode.of("12345"))
                .build();
    }
}