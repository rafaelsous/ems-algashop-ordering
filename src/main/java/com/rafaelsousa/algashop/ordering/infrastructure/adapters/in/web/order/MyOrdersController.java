package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.order;

import static com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.*;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityChecks;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.BuyNowInput;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.CheckoutInput;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ForBuyingProduct;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ForBuyingWithShoppingCart;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.*;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderSummaryOutput;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.PageModel;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler.UnprocessableEntityException;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/me/orders")
public class MyOrdersController {
    private final ForQueryingOrders forQueryingOrders;
    private final ForBuyingProduct forBuyingProduct;
    private final ForBuyingWithShoppingCart forBuyingWithShoppingCart;
    private final SecurityChecks securityChecks;

    @GetMapping("/{orderId}")
    @CanReadMyOrders
    public OrderDetailOutput findById(@PathVariable String orderId) {
        return forQueryingOrders.findByIdAndCustomerId(
                orderId, securityChecks.getAuthenticatedUserId());
    }

    @GetMapping
    @CanReadMyOrders
    public PageModel<OrderSummaryOutput> filter(OrderFilter filter) {
        filter.setCustomerId(securityChecks.getAuthenticatedUserId());
        return PageModel.of(forQueryingOrders.filter(filter));
    }

    @SneakyThrows
    @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    @CanWriteMyOrders
    public OrderDetailOutput create(@RequestBody @Valid BuyNowInput buyNowInput) {
	    if (Math.random() < 0.7) {
            Thread.sleep(Duration.ofMillis(100));
            throw new RuntimeException("Simulated error for order creation");
        }

        String orderId;
        buyNowInput.setCustomerId(securityChecks.getAuthenticatedUserId());

        try {
            orderId = forBuyingProduct.buyNow(buyNowInput);
        } catch (CustomerNotFoundException | ProductNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return forQueryingOrders.findById(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    @CanWriteMyOrders
    public OrderDetailOutput create(@RequestBody @Valid CheckoutInput checkoutInput) {
        String orderId;
        checkoutInput.setCustomerId(securityChecks.getAuthenticatedUserId());

        try {
            orderId = forBuyingWithShoppingCart.checkout(checkoutInput);
        } catch (CustomerNotFoundException | ShoppingCartNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return forQueryingOrders.findById(orderId);
    }
}
