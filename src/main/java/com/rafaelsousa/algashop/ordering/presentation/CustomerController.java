package com.rafaelsousa.algashop.ordering.presentation;

import com.rafaelsousa.algashop.ordering.application.commons.AddressData;
import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerInput;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOutput create(@RequestBody CustomerInput customerInput) {
        AddressData address = customerInput.getAddress();

        return CustomerOutput.builder()
                .id(UUID.randomUUID())
                .firstName(customerInput.getFirstName())
                .lastName(customerInput.getLastName())
                .document(customerInput.getDocument())
                .email(customerInput.getEmail())
                .phone(customerInput.getPhone())
                .birthDate(customerInput.getBirthDate())
                .promotionNotificationsAllowed(customerInput.getPromotionNotificationsAllowed())
                .registeredAt(OffsetDateTime.now())
                .archived(false)
                .loyaltyPoints(0)
                .address(address)
                .build();
    }
}