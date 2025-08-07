package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.ProductId;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderTestDataBuilder {
    private CustomerId customerId = new CustomerId();
    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;
    private Money shippingCost = Money.of(BigDecimal.ZERO);
    private LocalDate expectedDeliveryDate = LocalDate.now().plusWeeks(1);

    private ShippingInfo shipping = aShippingInfo();
    private BillingInfo billing = aBillingInfo();

    private boolean withItems = true;
    private OrderStatus status = OrderStatus.DRAFT;

    private OrderTestDataBuilder() { }

    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }

    public Order build() {
        Order order = Order.draft(customerId);
        order.changePaymentMethod(paymentMethod);
        order.changeShipping(shipping, shippingCost, expectedDeliveryDate);
        order.changeBilling(billing);

        if (withItems) {
            Product product1 = ProductTestDataBuilder.aProduct().build();

            Product product2 = ProductTestDataBuilder.aProductAltRamMemory().build();

            order.addItem(product1, Quantity.of(5));
            order.addItem(product2, Quantity.of(1));
        }

        switch (this.status) {
            case DRAFT -> {
            }
            case PLACED -> {
                order.place();
            }
            case PAID -> {
                order.place();
                order.markAsPaid();
            }
            case READY -> {
            }
            case CANCELED -> {
            }
        }

        return order;
    }

    public static ShippingInfo aShippingInfo() {
        return ShippingInfo.builder()
                .address(anAddress())
                .document(Document.of("123-12-1234"))
                .phone(Phone.of("123-123-1234"))
                .fullName(FullName.of("Rafael", "Sousa"))
                .build();
    }

    public static BillingInfo aBillingInfo() {
        return BillingInfo.builder()
                .address(anAddress())
                .document(Document.of("123-12-1234"))
                .phone(Phone.of("123-123-1234"))
                .fullName(FullName.of("Rafael", "Sousa"))
                .build();
    }

    public static Address anAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .neighborhood("North Ville")
                .number("123")
                .city("New York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .build();
    }

    public OrderTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTestDataBuilder paymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderTestDataBuilder shippingCost(Money shippingCost) {
        this.shippingCost = shippingCost;
        return this;
    }

    public OrderTestDataBuilder expectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
        return this;
    }

    public OrderTestDataBuilder shipping(ShippingInfo shipping) {
        this.shipping = shipping;
        return this;
    }

    public OrderTestDataBuilder billing(BillingInfo billing) {
        this.billing = billing;
        return this;
    }

    public OrderTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public OrderTestDataBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }
}