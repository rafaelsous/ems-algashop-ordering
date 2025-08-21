package com.rafaelsousa.algashop.ordering.application.service;

import com.rafaelsousa.algashop.ordering.application.model.AddressData;
import com.rafaelsousa.algashop.ordering.application.model.CustomerInput;
import com.rafaelsousa.algashop.ordering.domain.model.commons.*;
import com.rafaelsousa.algashop.ordering.domain.model.customer.BirthDate;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerRegistrationService;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {
    private final Customers customers;
    private final CustomerRegistrationService customerRegistrationService;

    @Transactional
    public UUID create(CustomerInput customerInput) {
        Objects.requireNonNull(customerInput);

        AddressData address = customerInput.getAddress();

        Customer customer = customerRegistrationService.register(
                FullName.of(customerInput.getFirstName(), customerInput.getLastName()),
                BirthDate.of(customerInput.getBithDate()),
                Email.of(customerInput.getEmail()),
                Phone.of(customerInput.getPhone()),
                Document.of(customerInput.getDocument()),
                customerInput.getPromotionNotificationsAllowed(),
                Address.builder()
                        .street(address.getStreet())
                        .number(address.getNumber())
                        .complement(address.getComplement())
                        .neighborhood(address.getNeighborhood())
                        .city(address.getCity())
                        .state(address.getState())
                        .zipCode(ZipCode.of(address.getZipCode()))
                        .build()
        );

        customers.add(customer);

        return customer.id().value();
    }
}