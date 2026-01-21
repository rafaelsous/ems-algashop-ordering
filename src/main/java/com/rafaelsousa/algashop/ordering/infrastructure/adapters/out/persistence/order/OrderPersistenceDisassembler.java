package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order;

import com.rafaelsousa.algashop.ordering.core.domain.model.CreditCardId;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.*;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.*;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductName;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.core.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddableDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order.shipping.RecipientEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order.shipping.ShippingEmbeddable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceDisassembler {

    public Order toDomain(OrderPersistence orderPersistence) {
        CreditCardId creditCardId = null;
        if (Objects.nonNull(orderPersistence.getCreditCardId())) {
            creditCardId = new CreditCardId(orderPersistence.getCreditCardId());
        }

        return Order.existing()
                .id(new OrderId(orderPersistence.getId()))
                .customerId(new CustomerId(orderPersistence.getCustomerId()))
                .totalAmount(Money.of(orderPersistence.getTotalAmount()))
                .totalItems(Quantity.of(orderPersistence.getTotalItems()))
                .paymentMethod(PaymentMethod.valueOf(orderPersistence.getPaymentMethod()))
                .creditCardId(creditCardId)
                .placedAt(orderPersistence.getPlacedAt())
                .paidAt(orderPersistence.getPaidAt())
                .readyAt(orderPersistence.getReadyAt())
                .canceledAt(orderPersistence.getCanceledAt())
                .status(OrderStatus.valueOf(orderPersistence.getStatus()))
                .items(this.buildItems(orderPersistence.getItems()))
                .version(orderPersistence.getVersion())
                .billing(this.buildBilling(orderPersistence.getBilling()))
                .shipping(this.buildShipping(orderPersistence.getShipping()))
                .build();
    }

    private Set<OrderItem> buildItems(Set<OrderItemPersistence> items) {
        if (Objects.isNull(items)) return new HashSet<>();

        return items.stream()
                .map(orderItemPersistence -> OrderItem.brandNew()
                        .orderId(new OrderId(orderItemPersistence.getOrderId()))
                        .product(Product.builder()
                                .id(new ProductId(orderItemPersistence.getProductId()))
                                .name(ProductName.of(orderItemPersistence.getProductName()))
                                .price(Money.of(orderItemPersistence.getPrice()))
                                .build()
                        )
                        .quantity(Quantity.of(orderItemPersistence.getQuantity()))
                        .build())
                .collect(Collectors.toSet());
    }

    private Billing buildBilling(BillingEmbeddable billing) {
        if (Objects.isNull(billing)) return null;

        return Billing.builder()
                .fullName(FullName.of(billing.getFirstName(), billing.getLastName()))
                .document(Document.of(billing.getDocument()))
                .phone(Phone.of(billing.getPhone()))
                .email(Email.of(billing.getEmail()))
                .address(AddressEmbeddableDisassembler.toDomain(billing.getAddress()))
                .build();
    }

    private Shipping buildShipping(ShippingEmbeddable shipping) {
        if (Objects.isNull(shipping)) return null;

        return Shipping.builder()
                .cost(Money.of(shipping.getCost()))
                .expectedDate(shipping.getExpectedDate())
                .recipient(this.buildRecipient(shipping.getRecipient()))
                .address(AddressEmbeddableDisassembler.toDomain(shipping.getAddress()))
                .build();
    }

    private Recipient buildRecipient(RecipientEmbeddable recipient) {
        Objects.requireNonNull(recipient);

        return Recipient.builder()
                .fullName(FullName.of(recipient.getFirstName(), recipient.getLastName()))
                .document(Document.of(recipient.getDocument()))
                .phone(Phone.of(recipient.getPhone()))
                .build();
    }
}