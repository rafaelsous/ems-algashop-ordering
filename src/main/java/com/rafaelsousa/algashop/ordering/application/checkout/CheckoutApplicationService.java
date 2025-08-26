package com.rafaelsousa.algashop.ordering.application.checkout;

import com.rafaelsousa.algashop.ordering.domain.model.commons.ZipCode;
import com.rafaelsousa.algashop.ordering.domain.model.order.*;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService {
    private final Orders orders;
    private final ShoppingCarts shoppingCarts;
    private final CheckoutService checkoutService;
    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    private final BillingInputDisassembler billingInputDisassembler;
    private final ShippingInputDisassembler shippingInputDisassembler;

    @Transactional
    public String checkout(CheckoutInput checkoutInput) {
        Objects.requireNonNull(checkoutInput);

        PaymentMethod paymentMethod = PaymentMethod.valueOf(checkoutInput.getPaymentMethod());
        ShoppingCartId shoppingCartId = new ShoppingCartId(checkoutInput.getShoppingCartId());

        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCartId));

        CalculationResponse calculationResponse = calculateShippingCost(checkoutInput.getShipping());

        Billing billing = billingInputDisassembler.toDomain(checkoutInput.getBilling());
        Shipping shipping = shippingInputDisassembler.toDomain(checkoutInput.getShipping(), calculationResponse);

        Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

        orders.add(order);
        shoppingCarts.add(shoppingCart);

        return order.id().toString();
    }

    private CalculationResponse calculateShippingCost(ShippingInput shippingInput) {
        ZipCode originZipCode = originAddressService.originAddress().zipCode();
        ZipCode destinationZipCode = ZipCode.of(shippingInput.getAddress().getZipCode());

        return shippingCostService.calculate(ShippingCostService.CalculationRequest.builder()
                .origin(originZipCode).destination(destinationZipCode).build());
    }
}