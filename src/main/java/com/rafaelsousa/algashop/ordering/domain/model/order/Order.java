package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.AbstractEventSourceEntity;
import com.rafaelsousa.algashop.ordering.domain.model.AggregateRoot;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Order
        extends AbstractEventSourceEntity
        implements AggregateRoot<OrderId> {
    private OrderId id;
    private CustomerId customerId;
    private Money totalAmount;
    private Quantity totalItems;

    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;

    private OrderStatus status;
    private PaymentMethod paymentMethod;

    private Billing billing;
    private Shipping shipping;

    private Set<OrderItem> items;

    private Long version;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    public Order(OrderId id, CustomerId customerId, Money totalAmount, Quantity totalItems, OffsetDateTime placedAt,
                 OffsetDateTime paidAt, OffsetDateTime canceledAt, OffsetDateTime readyAt, OrderStatus status,
                 PaymentMethod paymentMethod, Billing billing, Shipping shipping, Set<OrderItem> items, Long version) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCanceledAt(canceledAt);
        this.setReadyAt(readyAt);
        this.setStatus(status);
        this.setPaymentMethod(paymentMethod);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setItems(items);
        this.setVersion(version);
    }

    public static Order draft(CustomerId customerId) {
        return new Order(
                new OrderId(),
                customerId,
                Money.ZERO,
                Quantity.ZERO,
                null,
                null,
                null,
                null,
                OrderStatus.DRAFT,
                null,
                null,
                null,
                new HashSet<>(),
                null);
    }

    public void addItem(Product product, Quantity quantity) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);

        this.verifyChangeable();

        product.checkOutOfStock();

        OrderItem orderItem = OrderItem.brandNew()
                .orderId(this.id())
                .product(product)
                .quantity(quantity)
                .build();

        if (Objects.isNull(this.items)) {
            this.items = new HashSet<>();
        }

        this.items.add(orderItem);
        
        this.recalculateTotals();
    }

    public void removeItem(OrderItemId orderItemId) {
        Objects.requireNonNull(orderItemId);

        this.verifyChangeable();

        OrderItem orderItem = this.findOrderItem(orderItemId);

        this.items.remove(orderItem);

        this.recalculateTotals();
    }

    public void place() {
        this.verifyCanChangeToPlaced();

        this.changeStatus(OrderStatus.PLACED);
        this.setPlacedAt(OffsetDateTime.now());

        this.publishDomainEvent(OrderPlacedEvent.builder()
                .orderId(this.id())
                .customerId(this.customerId())
                .placedAt(this.placedAt())
                .build());
    }

    public void markAsPaid() {
        this.changeStatus(OrderStatus.PAID);
        this.setPaidAt(OffsetDateTime.now());

        this.publishDomainEvent(OrderPaidEvent.builder()
                .orderId(this.id())
                .customerId(this.customerId())
                .paidAt(this.paidAt())
                .build());
    }

    public void markAsReady() {
        this.changeStatus(OrderStatus.READY);
        this.setReadyAt(OffsetDateTime.now());

        this.publishDomainEvent(OrderReadyEvent.builder()
                .orderId(this.id())
                .customerId(this.customerId())
                .readyAt(this.readyAt())
                .build());
    }

    public void cancel() {
        this.changeStatus(OrderStatus.CANCELED);
        this.setCanceledAt(OffsetDateTime.now());

        this.publishDomainEvent(OrderCanceledEvent.builder()
                .orderId(this.id())
                .customerId(this.customerId())
                .canceledAt(this.canceledAt())
                .build());
    }

    public void changePaymentMethod(PaymentMethod paymentMethod) {
        Objects.requireNonNull(paymentMethod);

        this.verifyChangeable();

        this.setPaymentMethod(paymentMethod);
    }

    public void changeBilling(Billing billing) {
        Objects.requireNonNull(billing);

        this.verifyChangeable();

        this.setBilling(billing);
    }

    public void changeShipping(Shipping newShipping) {
        Objects.requireNonNull(newShipping);

        this.verifyChangeable();

        if (newShipping.expectedDate().isBefore(LocalDate.now())) {
            throw new OrderInvalidShippingDeliveryDateException(this.id());
        }

        this.setShipping(newShipping);
    }

    public void changeItemQuantity(OrderItemId orderItemId, Quantity quantity) {
        Objects.requireNonNull(orderItemId);
        Objects.requireNonNull(quantity);

        this.verifyChangeable();

        OrderItem orderItem = this.findOrderItem(orderItemId);
        orderItem.changeQuantity(quantity);

        this.recalculateTotals();
    }

    public boolean isDraft() {
        return OrderStatus.DRAFT.equals(this.status());
    }

    public boolean isPlaced() {
        return OrderStatus.PLACED.equals(this.status());
    }

    public boolean isPaid() {
        return OrderStatus.PAID.equals(this.status());
    }

    public boolean isReady() {
        return OrderStatus.READY.equals(this.status());
    }

    public boolean isCanceled() {
        return OrderStatus.CANCELED.equals(this.status());
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public OffsetDateTime placedAt() {
        return placedAt;
    }

    public OffsetDateTime paidAt() {
        return paidAt;
    }

    public OffsetDateTime canceledAt() {
        return canceledAt;
    }

    public OffsetDateTime readyAt() {
        return readyAt;
    }

    public OrderStatus status() {
        return status;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    public Billing billing() {
        return billing;
    }

    public Shipping shipping() {
        return shipping;
    }

    public Set<OrderItem> items() {
        return Collections.unmodifiableSet(this.items);
    }

    public Long version() {
        return version;
    }

    private void recalculateTotals() {
        BigDecimal totalItemsAmount = this.items().stream().map(i -> i.totalAmount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItemsQuantity = this.items().stream().map(i -> i.quantity().value())
                .reduce(0, Integer::sum);

        BigDecimal orderShippingCost;
        if (Objects.isNull(this.shipping)) {
            orderShippingCost = BigDecimal.ZERO;
        } else {
            orderShippingCost = this.shipping().cost().value();
        }

        BigDecimal total = totalItemsAmount.add(orderShippingCost);

        this.setTotalAmount(Money.of(total));
        this.setTotalItems(Quantity.of(totalItemsQuantity));
    }

    private void changeStatus(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus);

        if (this.status().canNotChangeTo(newStatus)) {
            throw new OrderStatusCannotBeChangedException(this.id(), this.status(), newStatus);
        }

        this.setStatus(newStatus);
    }

    private void verifyCanChangeToPlaced() {
        if (Objects.isNull(this.shipping())) {
            throw OrderCannotBePlacedException.noShippingInfo(this.id());
        }

        if (Objects.isNull(this.billing())) {
            throw OrderCannotBePlacedException.noBillingInfo(this.id());
        }

        if (Objects.isNull(this.paymentMethod())) {
            throw OrderCannotBePlacedException.noPaymentMethod(this.id());
        }

        if (Objects.isNull(this.items()) || this.items().isEmpty()) {
            throw OrderCannotBePlacedException.noItems(this.id());
        }
    }

    private OrderItem findOrderItem(OrderItemId orderItemId) {
        Objects.requireNonNull(orderItemId);

        return this.items().stream()
                .filter(i -> i.id().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderDoesNotContainItemException(this.id(), orderItemId));
    }

    private void verifyChangeable() {
        if (!this.isDraft()) {
            throw new OrderCannotBeEditedException(this.id(), this.status());
        }
    }

    private void setId(OrderId id) {
        Objects.requireNonNull(id);

        this.id = id;
    }

    private void setCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId);

        this.customerId = customerId;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);

        this.totalAmount = totalAmount;
    }

    private void setTotalItems(Quantity totalItems) {
        Objects.requireNonNull(totalItems);

        this.totalItems = totalItems;
    }

    private void setPlacedAt(OffsetDateTime placedAt) {
        this.placedAt = placedAt;
    }

    private void setPaidAt(OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    private void setCanceledAt(OffsetDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    private void setReadyAt(OffsetDateTime readyAt) {
        this.readyAt = readyAt;
    }

    private void setStatus(OrderStatus status) {
        Objects.requireNonNull(status);

        this.status = status;
    }

    private void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private void setBilling(Billing billing) {
        this.billing = billing;
    }

    private void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    private void setItems(Set<OrderItem> items) {
        Objects.requireNonNull(items);

        this.items = items;
    }

    private void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}