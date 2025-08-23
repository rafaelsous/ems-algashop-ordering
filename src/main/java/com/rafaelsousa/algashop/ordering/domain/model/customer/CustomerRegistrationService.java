package com.rafaelsousa.algashop.ordering.domain.model.customer;

import com.rafaelsousa.algashop.ordering.domain.model.commons.*;
import com.rafaelsousa.algashop.ordering.domain.model.DomainService;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class CustomerRegistrationService {
    private final Customers customers;

    public Customer register(FullName fullName, BirthDate birthDate, Email email, Phone phone,
                             Document document, Boolean promotionNotificationsAllowed, Address address) {
        Customer customer = Customer.brandNew()
                .fullName(fullName)
                .birthDate(birthDate)
                .email(email)
                .phone(phone)
                .document(document)
                .promotionNotificationsAllowed(promotionNotificationsAllowed)
                .address(address)
                .version(null)
                .build();

        verifyEmailUniqueness(customer.email(), customer.id());

        return customer;
    }

    public void changeEmail(Customer customer, Email newEmail) {
        verifyEmailUniqueness(newEmail, customer.id());
        customer.changeEmail(newEmail);
    }

    private void verifyEmailUniqueness(Email email, CustomerId customerId) {
        if (!customers.isEmailUnique(email, customerId)) {
            throw new CustomerEmailAlreadyExistsException(email);
        }
    }
}