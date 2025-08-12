package com.rafaelsousa.algashop.ordering.domain.model.repository;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;

public interface Customers extends Repository<Customer, CustomerId> {
}