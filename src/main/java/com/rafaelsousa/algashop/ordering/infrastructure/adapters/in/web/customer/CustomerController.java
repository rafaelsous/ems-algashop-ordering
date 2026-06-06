package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.customer;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.rafaelsousa.algashop.ordering.core.ports.in.customer.*;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ForQueryingShoppingCarts;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ShoppingCartOutput;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.PageModel;
import com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.CanReadCustomers;
import com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.CanWriteCustomers;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final ForManagingCustomers forManagingCustomers;
    private final ForQueryingCustomers forQueryingCustomers;
    private final ForQueryingShoppingCarts forQueryingShoppingCarts;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CanWriteCustomers
    public CustomerOutput create(@RequestBody @Valid CustomerInput customerInput, HttpServletResponse httpServletResponse) {
        UUID customerId = forManagingCustomers.create(customerInput);

        UriComponentsBuilder uriComponentsBuilder = fromMethodCall(on(CustomerController.class).findById(customerId));
        httpServletResponse.setHeader(HttpHeaders.LOCATION, uriComponentsBuilder.toUriString());

        return forQueryingCustomers.findById(customerId);
    }

    @GetMapping
    @CanReadCustomers
    public PageModel<CustomerSummaryOutput> findAll(CustomerFilter customerFilter) {
        return PageModel.of(forQueryingCustomers.filter(customerFilter));
    }

    @GetMapping("/{customerId}")
    @CanReadCustomers
    public CustomerOutput findById(@PathVariable UUID customerId) {
        return forQueryingCustomers.findById(customerId);
    }

    @GetMapping("/{customerId}/shopping-cart")
    @CanReadCustomers
    public ShoppingCartOutput findShoppingCartByCustomerId(@PathVariable UUID customerId) {
        return forQueryingShoppingCarts.findByCustomerId(customerId);
    }

    @PutMapping("/{customerId}")
    @CanWriteCustomers
    public CustomerOutput update(@PathVariable UUID customerId, @RequestBody @Valid CustomerUpdateInput customerUpdateInput) {
        forManagingCustomers.update(customerId, customerUpdateInput);

        return forQueryingCustomers.findById(customerId);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CanWriteCustomers
    public void delete(@PathVariable UUID customerId) {
        forManagingCustomers.archive(customerId);
    }
}