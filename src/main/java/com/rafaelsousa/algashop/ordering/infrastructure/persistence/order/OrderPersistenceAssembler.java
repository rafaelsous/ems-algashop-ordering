package com.rafaelsousa.algashop.ordering.infrastructure.persistence.order;

import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderItem;
import com.rafaelsousa.algashop.ordering.domain.model.order.Billing;
import com.rafaelsousa.algashop.ordering.domain.model.order.Recipient;
import com.rafaelsousa.algashop.ordering.domain.model.order.Shipping;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddableAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.shipping.RecipientEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.shipping.ShippingEmbeddable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAssembler {
    private final CustomerPersistenceRepository customerPersistenceRepository;

    public OrderPersistence fromDomain(Order order) {
        return merge(new OrderPersistence(), order);
    }

    public OrderPersistence merge(OrderPersistence orderPersistence, Order aggregateRoot) {
        orderPersistence.setId(aggregateRoot.id().value().toLong());
        orderPersistence.setTotalAmount(aggregateRoot.totalAmount().value());
        orderPersistence.setTotalItems(aggregateRoot.totalItems().value());
        orderPersistence.setStatus(aggregateRoot.status().name());
        orderPersistence.setPaymentMethod(aggregateRoot.paymentMethod().name());
        orderPersistence.setPlacedAt(aggregateRoot.placedAt());
        orderPersistence.setPaidAt(aggregateRoot.paidAt());
        orderPersistence.setCanceledAt(aggregateRoot.canceledAt());
        orderPersistence.setReadyAt(aggregateRoot.readyAt());
        orderPersistence.setVersion(aggregateRoot.version());
        orderPersistence.setBilling(this.buildBilling(aggregateRoot.billing()));
        orderPersistence.setShipping(this.buildShipping(aggregateRoot.shipping()));

        CustomerPersistence customerPersistence = customerPersistenceRepository.getReferenceById(aggregateRoot.customerId().value());
        orderPersistence.setCustomer(customerPersistence);

        Set<OrderItemPersistence> mergedItems = mergeItems(orderPersistence, aggregateRoot);
        orderPersistence.replaceItems(mergedItems);
        orderPersistence.addEvents(aggregateRoot.domainEvents());

        return orderPersistence;
    }

    public OrderItemPersistence fromDomain(OrderItem orderItem) {
        return merge(new OrderItemPersistence(), orderItem);
    }

    private Set<OrderItemPersistence> mergeItems(OrderPersistence orderPersistence, Order order) {
        Set<OrderItem> newOrUpdatedItems = order.items();

        if (CollectionUtils.isEmpty(newOrUpdatedItems)) {
            return new HashSet<>();
        }

        Set<OrderItemPersistence> existingItems = orderPersistence.getItems();
        if (CollectionUtils.isEmpty(existingItems)) {
            return newOrUpdatedItems.stream()
                    .map(this::fromDomain)
                    .collect(Collectors.toSet());
        }

        Map<Long, OrderItemPersistence> existingItemsMap = existingItems.stream()
                .collect(Collectors.toMap(OrderItemPersistence::getId, item -> item));

        return newOrUpdatedItems.stream()
                .map(orderItem -> {
                    OrderItemPersistence itemPersistence = existingItemsMap
                            .getOrDefault(orderItem.id().value().toLong(), new OrderItemPersistence());

                    return merge(itemPersistence, orderItem);
                }).collect(Collectors.toSet());
    }

    private OrderItemPersistence merge(OrderItemPersistence orderItemPersistence, OrderItem orderItem) {
        orderItemPersistence.setId(orderItem.id().value().toLong());
        orderItemPersistence.setProductId(orderItem.productId().value());
        orderItemPersistence.setProductName(orderItem.productName().value());
        orderItemPersistence.setPrice(orderItem.price().value());
        orderItemPersistence.setQuantity(orderItem.quantity().value());
        orderItemPersistence.setTotalAmount(orderItem.totalAmount().value());

        return orderItemPersistence;
    }

    private BillingEmbeddable buildBilling(Billing billing) {
        if (Objects.isNull(billing)) return null;

        return BillingEmbeddable.builder()
                .firstName(billing.fullName().firstName())
                .lastName(billing.fullName().lastName())
                .document(billing.document().value())
                .phone(billing.phone().value())
                .email(billing.email().value())
                .address(
                        AddressEmbeddableAssembler.fromPersistence(billing.address())
                )
                .build();
    }

    private ShippingEmbeddable buildShipping(Shipping shipping) {
        if (Objects.isNull(shipping)) return null;

        return ShippingEmbeddable.builder()
                .cost(shipping.cost().value())
                .expectedDate(shipping.expectedDate())
                .recipient(buildRecipient(shipping.recipient()))
                .address(
                        AddressEmbeddableAssembler.fromPersistence(shipping.address())
                )
                .build();
    }

    private RecipientEmbeddable buildRecipient(Recipient recipient) {
        Objects.requireNonNull(recipient);

        return RecipientEmbeddable.builder()
                .firstName(recipient.fullName().firstName())
                .lastName(recipient.fullName().lastName())
                .document(recipient.document().value())
                .phone(recipient.phone().value())
                .build();
    }
}