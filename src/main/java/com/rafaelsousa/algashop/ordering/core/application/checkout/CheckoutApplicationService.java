package com.rafaelsousa.algashop.ordering.core.application.checkout;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityChecks;
import com.rafaelsousa.algashop.ordering.core.domain.model.CreditCardId;
import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.ZipCode;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.*;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.OriginAddressService;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.shoppingcart.ShoppingCarts;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ShippingInput;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.CheckoutInput;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ForBuyingWithShoppingCart;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.checkout.BillingInputDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.checkout.ShippingInputDisassembler;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService implements ForBuyingWithShoppingCart {
    private final Orders orders;
    private final Customers customers;
    private final ShoppingCarts shoppingCarts;
    private final CheckoutService checkoutService;
    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    private final BillingInputDisassembler billingInputDisassembler;
    private final ShippingInputDisassembler shippingInputDisassembler;
    private final SecurityChecks securityChecks;

    @Transactional
    @Override
    public String checkout(CheckoutInput checkoutInput) {
        Objects.requireNonNull(checkoutInput);

        PaymentMethod paymentMethod = PaymentMethod.valueOf(checkoutInput.getPaymentMethod());

        CreditCardId creditCardId = null;

        if (paymentMethod.equals(PaymentMethod.CREDIT_CARD)) {
            if (Objects.isNull(checkoutInput.getCreditCardId())) {
                throw new DomainException("Credit card is required");
            }

            creditCardId = new CreditCardId(checkoutInput.getCreditCardId());
        }

        ShoppingCartId shoppingCartId = new ShoppingCartId(checkoutInput.getShoppingCartId());

        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCartId));

        CustomerId customerId = shoppingCart.customerId();

        verifyCanOrderFor(customerId.value());

        Customer customer = customers.ofId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        CalculationResponse calculationResponse = calculateShippingCost(checkoutInput.getShipping());

        Billing billing = billingInputDisassembler.toDomain(checkoutInput.getBilling());
        Shipping shipping = shippingInputDisassembler.toDomain(checkoutInput.getShipping(), calculationResponse);

        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod, creditCardId);

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

    private void verifyCanOrderFor(@NotNull UUID customerId) {
        if (!securityChecks.canOrderFor(customerId)) {
            throw new AccessDeniedException("Cannot order for customer " + customerId);
        }
    }
}