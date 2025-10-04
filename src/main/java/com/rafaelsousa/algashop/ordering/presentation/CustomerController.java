package com.rafaelsousa.algashop.ordering.presentation;

import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerInput;
import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerManagementApplicationService customerManagementApplicationService;
    private final CustomerQueryService customerQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOutput create(@RequestBody CustomerInput customerInput) {
        UUID customerId = customerManagementApplicationService.create(customerInput);

        return customerQueryService.findById(customerId);
    }
}