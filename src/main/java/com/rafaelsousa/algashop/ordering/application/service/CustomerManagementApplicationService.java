package com.rafaelsousa.algashop.ordering.application.service;

import com.rafaelsousa.algashop.ordering.application.model.AddressData;
import com.rafaelsousa.algashop.ordering.application.model.CustomerInput;
import com.rafaelsousa.algashop.ordering.application.model.CustomerOutput;
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

        return CustomerOutput.builder()
                .id(customer.id().value())
                .firstName(customer.fullName().firstName())
                .lastName(customer.fullName().lastName())
                .birthDate(Objects.nonNull(customer.birthDate().value()) ? customer.birthDate().value() : null)
                .email(customer.email().value())
                .document(customer.document().value())
                .phone(customer.phone().value())
                .promotionNotificationsAllowed(customer.isPromotionNotificationsAllowed())
                .archived(customer.isArchived())
                .archivedAt(Objects.nonNull(customer.archivedAt()) ? customer.archivedAt() : null)
                .registeredAt(customer.registeredAt())
                .loyaltyPoints(customer.loyaltyPoints().value())
                .addressData(AddressData.builder()
                        .street(customer.address().street())
                        .number(customer.address().number())
                        .complement(customer.address().complement())
                        .neighborhood(customer.address().neighborhood())
                        .city(customer.address().city())
                        .state(customer.address().state())
                        .zipCode(customer.address().zipCode().value())
                        .build())
                .build();
    }
}