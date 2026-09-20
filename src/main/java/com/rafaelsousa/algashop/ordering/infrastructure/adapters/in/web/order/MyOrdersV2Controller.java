package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.order;

import static com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.*;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityChecks;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.*;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.UnprocessableEntityException;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/customers/me/orders")
public class MyOrdersV2Controller {
    private final ForBuyingProductAsync forBuyingProductAsync;
    private final ForBuyingWithShoppingCartAsync forBuyingWithShoppingCartAsync;
    private final SecurityChecks securityChecks;

    @SneakyThrows
    @PostMapping(consumes = "application/vnd.order-with-product.v2+json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @CanWriteMyOrders
    public OrderAcceptedOutput create(@RequestBody @Valid BuyNowInput buyNowInput) {
        String orderId;
        buyNowInput.setCustomerId(securityChecks.getAuthenticatedUserId());

        try {
            orderId = forBuyingProductAsync.buyNow(buyNowInput);
        } catch (CustomerNotFoundException | ProductNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return new OrderAcceptedOutput(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v2+json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @CanWriteMyOrders
    public OrderAcceptedOutput create(@RequestBody @Valid CheckoutInput checkoutInput) {
        String orderId;
        checkoutInput.setCustomerId(securityChecks.getAuthenticatedUserId());

        try {
            orderId = forBuyingWithShoppingCartAsync.checkout(checkoutInput);
        } catch (CustomerNotFoundException | ShoppingCartNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return new OrderAcceptedOutput(orderId);
    }
}
