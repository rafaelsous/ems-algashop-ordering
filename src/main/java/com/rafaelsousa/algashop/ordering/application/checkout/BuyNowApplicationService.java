package com.rafaelsousa.algashop.ordering.application.checkout;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.commons.ZipCode;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.order.*;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BuyNowApplicationService {
    private final BuyNowService buyNowService;
    private final ProductCatalogService productCatalogService;
    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    private final Orders orders;
    private final BillingInputDisassembler billingInputDisassembler;
    private final ShippingInputDisassembler shippingInputDisassembler;

    @Transactional
    public String buyNow(BuyNowInput buyNowInput) {
        Objects.requireNonNull(buyNowInput);

        CustomerId customerId = new CustomerId(buyNowInput.getCustomerId());
        Quantity quantity = Quantity.of(buyNowInput.getQuantity());
        PaymentMethod paymentMethod = PaymentMethod.valueOf(buyNowInput.getPaymentMethod());
        ProductId productId = new ProductId(buyNowInput.getProductId());

        Product product = findProduct(productId);
        CalculationResponse calculationResponse = calculateShippingCost(buyNowInput.getShipping());

        Billing billing = billingInputDisassembler.toDomain(buyNowInput.getBilling());
        Shipping shipping = shippingInputDisassembler.toDomain(buyNowInput.getShipping(), calculationResponse);

        Order order = buyNowService.buyNow(product, customerId, billing, shipping, quantity, paymentMethod);

        orders.add(order);

        return order.id().toString();
    }

    private CalculationResponse calculateShippingCost(ShippingInput shippingInput) {
        ZipCode originZipCode = originAddressService.originAddress().zipCode();
        ZipCode destinationZipCode = ZipCode.of(shippingInput.getAddress().getZipCode());

        return shippingCostService.calculate(CalculationRequest.builder()
                .origin(originZipCode).destination(destinationZipCode).build());
    }

    private Product findProduct(ProductId productId) {
        return productCatalogService.ofId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}