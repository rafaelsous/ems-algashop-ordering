package com.rafaelsousa.algashop.ordering.core.domain.model.customer;

import com.rafaelsousa.algashop.ordering.core.domain.model.Repository;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Email;

import java.util.Optional;

public interface Customers extends Repository<Customer, CustomerId> {
    Optional<Customer> ofEmail(Email email);
    boolean isEmailUnique(Email email, CustomerId exceptCustomerId);
}