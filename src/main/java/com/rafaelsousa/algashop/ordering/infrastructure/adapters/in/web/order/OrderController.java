package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.order;

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
import com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.CanReadOrders;
import com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.CanWriteOrders;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final ForQueryingOrders forQueryingOrders;
    private final ForBuyingProduct forBuyingProduct;
    private final ForBuyingWithShoppingCart forBuyingWithShoppingCart;

    @GetMapping("/{orderId}")
    @CanReadOrders
    public OrderDetailOutput findById(@PathVariable("orderId") String orderId) {
        return forQueryingOrders.findById(orderId);
    }

    @GetMapping
    @CanReadOrders
    public PageModel<OrderSummaryOutput> filter(OrderFilter filter) {
        return PageModel.of(forQueryingOrders.filter(filter));
    }

    @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    @CanWriteOrders
    public OrderDetailOutput create(@RequestBody @Valid BuyNowInput buyNowInput) {
        String orderId;

        try {
            orderId = forBuyingProduct.buyNow(buyNowInput);
        } catch (CustomerNotFoundException | ProductNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return forQueryingOrders.findById(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    @CanWriteOrders
    public OrderDetailOutput create(@RequestBody @Valid CheckoutInput checkoutInput) {
        String orderId;

        try {
            orderId = forBuyingWithShoppingCart.checkout(checkoutInput);
        } catch (CustomerNotFoundException | ShoppingCartNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return forQueryingOrders.findById(orderId);
    }
}