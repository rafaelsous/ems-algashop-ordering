package com.rafaelsousa.algashop.ordering.core.ports.out.customer;

import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerFilter;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerSummaryOutput;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ForObtainingCustomers {
    CustomerOutput findById(UUID customerId);
    Page<CustomerSummaryOutput> filter(CustomerFilter filter);
}