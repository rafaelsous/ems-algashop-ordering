package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerQueryService;
import com.rafaelsousa.algashop.ordering.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerQueryServiceImpl implements CustomerQueryService {
    private final CustomerPersistenceRepository customerPersistenceRepository;
    private final Mapper mapper;

    @Override
    public CustomerOutput findById(UUID rawCustomerId) {
        Objects.requireNonNull(rawCustomerId);

        CustomerId customerId = new CustomerId(rawCustomerId);
        CustomerPersistence customerPersistence = customerPersistenceRepository.findById(rawCustomerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return mapper.convert(customerPersistence, CustomerOutput.class);
    }
}