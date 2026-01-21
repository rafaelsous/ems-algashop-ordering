package com.rafaelsousa.algashop.ordering.core.ports.in.customer;

import java.util.UUID;

public interface ForManagingCustomers {
    UUID create(CustomerInput customerInput);
    void update(UUID rawCustomerId, CustomerUpdateInput customerUpdateInput);
    void archive(UUID rawCustomerId);
    void changeEmail(UUID rawCustomerId, String newEmail);
}