package com.rafaelsousa.algashop.ordering.application.customer.management;

import com.rafaelsousa.algashop.ordering.application.commons.AddressData;
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

    @Transactional
    public UUID create(CustomerInput customerInput) {
        Objects.requireNonNull(customerInput);

        AddressData address = customerInput.getAddress();

        Customer customer = customerRegistrationService.register(
                FullName.of(customerInput.getFirstName(), customerInput.getLastName()),
                BirthDate.of(customerInput.getBirthDate()),
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

    @Transactional
    public void update(UUID rawCustomerId, CustomerUpdateInput customerUpdateInput) {
        Objects.requireNonNull(rawCustomerId);
        Objects.requireNonNull(customerUpdateInput);

        CustomerId customerId = new CustomerId(rawCustomerId);
        Customer customer = customers.ofId(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

        customer.changeName(FullName.of(customerUpdateInput.getFirstName(), customerUpdateInput.getLastName()));
        customer.changePhone(Phone.of(customerUpdateInput.getPhone()));

        if (Boolean.TRUE.equals(customerUpdateInput.getPromotionNotificationsAllowed())) {
            customer.enablePromotionNotifications();
        } else {
            customer.disablePromotionNotifications();
        }

        AddressData address = customerUpdateInput.getAddress();
        customer.changeAddress(Address.builder()
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
    }

    @Transactional
    public void archive(UUID rawCustomerId) {
        Objects.requireNonNull(rawCustomerId);

        CustomerId customerId = new CustomerId(rawCustomerId);
        Customer customer = customers.ofId(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

        customer.archive();

        customers.add(customer);
    }

    @Transactional
    public void changeEmail(UUID rawCustomerId, String newEmail) {
        Objects.requireNonNull(rawCustomerId);
        Objects.requireNonNull(newEmail);

        CustomerId customerId = new CustomerId(rawCustomerId);
        Customer customer = customers.ofId(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

        customerRegistrationService.changeEmail(customer, Email.of(newEmail));

        customers.add(customer);
    }
}