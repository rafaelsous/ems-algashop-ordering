package com.rafaelsousa.algashop.ordering.core.application.checkout;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityCheckApplicationService;
import com.rafaelsousa.algashop.ordering.core.domain.model.CreditCardId;
import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.ZipCode;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.*;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.OriginAddressService;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ShippingInput;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.BuyNowInput;
import com.rafaelsousa.algashop.ordering.core.ports.in.checkout.ForBuyingProduct;
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
public class BuyNowApplicationService implements ForBuyingProduct {
    private final BuyNowService buyNowService;
    private final ProductCatalogService productCatalogService;
    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    private final Orders orders;
    private final Customers customers;
    private final BillingInputDisassembler billingInputDisassembler;
    private final ShippingInputDisassembler shippingInputDisassembler;
    private final SecurityCheckApplicationService securityCheckApplicationService;

    @Override
    @Transactional
    public String buyNow(BuyNowInput buyNowInput) {
        Objects.requireNonNull(buyNowInput);

        verifyCanOrderFor(buyNowInput.getCustomerId());

        CustomerId customerId = new CustomerId(buyNowInput.getCustomerId());
        Quantity quantity = Quantity.of(buyNowInput.getQuantity());
        PaymentMethod paymentMethod = PaymentMethod.valueOf(buyNowInput.getPaymentMethod());
        ProductId productId = new ProductId(buyNowInput.getProductId());

        CreditCardId creditCardId = null;

        if (paymentMethod.equals(PaymentMethod.CREDIT_CARD)) {
            if (Objects.isNull(buyNowInput.getCreditCardId())) {
                throw new DomainException("Credit card is required");
            }

            creditCardId = new CreditCardId(buyNowInput.getCreditCardId());
        }

        Customer customer = customers.ofId(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
        Product product = productCatalogService.ofId(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        CalculationResponse calculationResponse = calculateShippingCost(buyNowInput.getShipping());

        Billing billing = billingInputDisassembler.toDomain(buyNowInput.getBilling());
        Shipping shipping = shippingInputDisassembler.toDomain(buyNowInput.getShipping(), calculationResponse);

        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod, creditCardId);

        orders.add(order);

        return order.id().toString();
    }

    private void verifyCanOrderFor(@NotNull UUID customerId) {
        if (!(securityCheckApplicationService.isCustomer() && securityCheckApplicationService.getAuthenticatedUserId().equals(customerId))) {
            throw new AccessDeniedException("Cannot order for customer " + customerId);
        }
    }

    private CalculationResponse calculateShippingCost(ShippingInput shippingInput) {
        ZipCode originZipCode = originAddressService.originAddress().zipCode();
        ZipCode destinationZipCode = ZipCode.of(shippingInput.getAddress().getZipCode());

        return shippingCostService.calculate(CalculationRequest.builder()
                .origin(originZipCode).destination(destinationZipCode).build());
    }
}