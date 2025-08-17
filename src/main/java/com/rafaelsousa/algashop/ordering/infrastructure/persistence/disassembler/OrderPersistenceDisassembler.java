package com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderItem;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.entity.PaymentMethod;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceDisassembler {

    public Order toDomain(OrderPersistence orderPersistence) {
        return Order.existing()
                .id(new OrderId(orderPersistence.getId()))
                .customerId(new CustomerId(orderPersistence.getCustomerId()))
                .totalAmount(Money.of(orderPersistence.getTotalAmount()))
                .totalItems(Quantity.of(orderPersistence.getTotalItems()))
                .paymentMethod(PaymentMethod.valueOf(orderPersistence.getPaymentMethod()))
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