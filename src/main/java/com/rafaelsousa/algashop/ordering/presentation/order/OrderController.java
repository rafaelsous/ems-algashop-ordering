package com.rafaelsousa.algashop.ordering.presentation.order;

import com.rafaelsousa.algashop.ordering.application.checkout.BuyNowApplicationService;
import com.rafaelsousa.algashop.ordering.application.checkout.BuyNowInput;
import com.rafaelsousa.algashop.ordering.application.checkout.CheckoutApplicationService;
import com.rafaelsousa.algashop.ordering.application.checkout.CheckoutInput;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderFilter;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderQueryService;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.rafaelsousa.algashop.ordering.presentation.PageModel;
import com.rafaelsousa.algashop.ordering.presentation.UnprocessableEntityException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderQueryService orderQueryService;
    private final BuyNowApplicationService buyNowApplicationService;
    private final CheckoutApplicationService checkoutApplicationService;

    @GetMapping("/{orderId}")
    public OrderDetailOutput findById(@PathVariable("orderId") String orderId) {
        return orderQueryService.findById(orderId);
    }

    @GetMapping
    public PageModel<OrderSummaryOutput> filter(OrderFilter filter) {
        return PageModel.of(orderQueryService.filter(filter));
    }

    @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetailOutput create(@RequestBody @Valid BuyNowInput buyNowInput) {
        String orderId;

        try {
            orderId = buyNowApplicationService.buyNow(buyNowInput);
        } catch (CustomerNotFoundException | ProductNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return orderQueryService.findById(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetailOutput create(@RequestBody @Valid CheckoutInput checkoutInput) {
        String orderId;

        try {
            orderId= checkoutApplicationService.checkout(checkoutInput);
        } catch (CustomerNotFoundException | ShoppingCartNotFoundException ex) {
            throw new UnprocessableEntityException(ex.getMessage(), ex);
        }

        return orderQueryService.findById(orderId);
    }
}