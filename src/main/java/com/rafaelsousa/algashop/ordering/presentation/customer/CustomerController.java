package com.rafaelsousa.algashop.ordering.presentation.customer;

import com.rafaelsousa.algashop.ordering.core.application.customer.management.CustomerInput;
import com.rafaelsousa.algashop.ordering.core.application.customer.management.CustomerManagementApplicationService;
import com.rafaelsousa.algashop.ordering.core.application.customer.management.CustomerUpdateInput;
import com.rafaelsousa.algashop.ordering.core.application.customer.query.CustomerFilter;
import com.rafaelsousa.algashop.ordering.core.application.customer.query.CustomerOutput;
import com.rafaelsousa.algashop.ordering.core.application.customer.query.CustomerQueryService;
import com.rafaelsousa.algashop.ordering.core.application.customer.query.CustomerSummaryOutput;
import com.rafaelsousa.algashop.ordering.core.application.shoppingcart.query.ShoppingCartOutput;
import com.rafaelsousa.algashop.ordering.core.application.shoppingcart.query.ShoppingCartQueryService;
import com.rafaelsousa.algashop.ordering.presentation.PageModel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerManagementApplicationService customerManagementApplicationService;
    private final CustomerQueryService customerQueryService;
    private final ShoppingCartQueryService shoppingCartQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOutput create(@RequestBody @Valid CustomerInput customerInput, HttpServletResponse httpServletResponse) {
        UUID customerId = customerManagementApplicationService.create(customerInput);

        UriComponentsBuilder uriComponentsBuilder = fromMethodCall(on(CustomerController.class).findById(customerId));
        httpServletResponse.setHeader(HttpHeaders.LOCATION, uriComponentsBuilder.toUriString());

        return customerQueryService.findById(customerId);
    }

    @GetMapping
    public PageModel<CustomerSummaryOutput> findAll(CustomerFilter customerFilter) {
        return PageModel.of(customerQueryService.filter(customerFilter));
    }

    @GetMapping("/{customerId}")
    public CustomerOutput findById(@PathVariable UUID customerId) {
        return customerQueryService.findById(customerId);
    }

    @GetMapping("/{customerId}/shopping-cart")
    public ShoppingCartOutput findShoppingCartByCustomerId(@PathVariable UUID customerId) {
        return shoppingCartQueryService.findByCustomerId(customerId);
    }

    @PutMapping("/{customerId}")
    public CustomerOutput update(@PathVariable UUID customerId, @RequestBody @Valid CustomerUpdateInput customerUpdateInput) {
        customerManagementApplicationService.update(customerId, customerUpdateInput);

        return customerQueryService.findById(customerId);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID customerId) {
        customerManagementApplicationService.archive(customerId);
    }
}