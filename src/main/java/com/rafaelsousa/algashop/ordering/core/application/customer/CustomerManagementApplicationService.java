package com.rafaelsousa.algashop.ordering.core.application.customer;

import com.rafaelsousa.algashop.ordering.core.ports.commons.AddressData;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.*;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.*;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerInput;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerUpdateInput;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.ForManagingCustomers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService implements ForManagingCustomers {
    private final Customers customers;
    private final CustomerRegistrationService customerRegistrationService;

    @Override
    @Transactional
    public UUID create(UUID customerUserId, CustomerInput customerInput) {
        Objects.requireNonNull(customerInput);

        AddressData address = customerInput.getAddress();

        Customer customer = customerRegistrationService.register(
                CustomerId.of(customerUserId),
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

    @Override
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

    @Override
    @Transactional
    public void archive(UUID rawCustomerId) {
        Objects.requireNonNull(rawCustomerId);

        CustomerId customerId = new CustomerId(rawCustomerId);
        Customer customer = customers.ofId(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

        customer.archive();

        customers.add(customer);
    }

    @Override
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