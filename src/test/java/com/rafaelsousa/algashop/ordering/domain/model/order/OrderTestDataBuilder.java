package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.commons.*;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;

import java.time.LocalDate;

import static com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class OrderTestDataBuilder {
    private CustomerId customerId = DEFAULT_CUSTOMER_ID;
    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;

    private Shipping shipping = aShipping();
    private Billing billing = aBilling();

    private boolean withItems = true;
    private OrderStatus status = OrderStatus.DRAFT;

    private OrderTestDataBuilder() { }

    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }

    public Order build() {
        Order order = Order.draft(this.customerId);
        order.changePaymentMethod(this.paymentMethod);
        order.changeShipping(this.shipping);
        order.changeBilling(this.billing);

        if (withItems) {
            Product product1 = ProductTestDataBuilder.aProduct().build();

            Product product2 = ProductTestDataBuilder.aProductAltRamMemory().build();

            order.addItem(product1, Quantity.of(5));
            order.addItem(product2, Quantity.of(1));
        }

        switch (this.status) {
            case DRAFT -> {
            }
            case PLACED -> order.place();
            case PAID -> {
                order.place();
                order.markAsPaid();
            }
            case READY -> {
                order.place();
                order.markAsPaid();
                order.markAsReady();
            }
            case CANCELED -> order.cancel();
        }

        return order;
    }

    public static Shipping aShipping() {
        return Shipping.builder()
                .address(anAddress())
                .recipient(Recipient.builder()
                        .fullName(FullName.of("Rafael", "Sousa"))
                        .document(Document.of("123-12-1234"))
                        .phone(Phone.of("123-123-1234"))
                        .build())
                .cost(Money.of("10.00"))
                .expectedDate(LocalDate.now().plusWeeks(1))
                .build();
    }

    public static Shipping aShippingAlt() {
        return Shipping.builder()
                .address(anAddressAlt())
                .recipient(Recipient.builder()
                        .fullName(FullName.of("Tatiane", "Sousa"))
                        .document(Document.of("456-45-4567"))
                        .phone(Phone.of("456-456-4567"))
                        .build())
                .cost(Money.of("25.00"))
                .expectedDate(LocalDate.now().plusWeeks(3))
                .build();
    }

    public static Billing aBilling() {
        return Billing.builder()
                .address(anAddress())
                .document(Document.of("123-12-1234"))
                .phone(Phone.of("123-123-1234"))
                .fullName(FullName.of("Rafael", "Sousa"))
                .email(Email.of("rafael.sousa@email.com"))
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

    public static Address anAddressAlt() {
        return Address.builder()
                .street("Sansome Street")
                .neighborhood("Sansome")
                .number("789")
                .city("San Francisco")
                .state("California")
                .zipCode(new ZipCode("45678"))
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

    public OrderTestDataBuilder shipping(Shipping shipping) {
        this.shipping = shipping;
        return this;
    }

    public OrderTestDataBuilder billing(Billing billing) {
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