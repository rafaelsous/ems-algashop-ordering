package com.rafaelsousa.algashop.ordering.domain.factory;

import com.rafaelsousa.algashop.ordering.domain.entity.*;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Billing;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Shipping;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderFactoryTest {

    @Test
    void shouldGenerateFilledOrderThatCanBePlaced() {
        CustomerId customerId = new CustomerId();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Billing billing = OrderTestDataBuilder.aBilling();
        PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;
        Product product = ProductTestDataBuilder.aProduct().build();
        Quantity productQuantity = Quantity.of(1);

        Order order = OrderFactory.filled(customerId, shipping, billing, paymentMethod, product, productQuantity);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.customerId()).isEqualTo(customerId),
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shipping),
                o -> Assertions.assertThat(o.billing()).isEqualTo(billing),
                o -> Assertions.assertThat(o.paymentMethod()).isEqualTo(paymentMethod),
                o -> Assertions.assertThat(o.items()).isNotEmpty(),
                o -> Assertions.assertThat(o.items()).hasSize(1),
                o -> Assertions.assertThat(o.status()).isEqualTo(OrderStatus.DRAFT)
        );

        order.place();

        Assertions.assertThat(order.isPlaced()).isTrue();
    }
}