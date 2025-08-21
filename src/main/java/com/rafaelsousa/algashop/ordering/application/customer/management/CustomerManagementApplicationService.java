package com.rafaelsousa.algashop.ordering.application.customer.management;

import com.rafaelsousa.algashop.ordering.application.commons.AddressData;
import com.rafaelsousa.algashop.ordering.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.domain.model.commons.*;
import com.rafaelsousa.algashop.ordering.domain.model.customer.*;
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
    private final Mapper mapper;

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

    public CustomerOutput findById(UUID customerId) {
        Objects.requireNonNull(customerId);

        CustomerId id = new CustomerId(customerId);
        Customer customer = customers.ofId(id).orElseThrow(() -> new CustomerNotFoundException(id));

        return mapper.convert(customer, CustomerOutput.class);
    }
}