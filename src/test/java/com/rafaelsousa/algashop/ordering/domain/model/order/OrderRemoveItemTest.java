package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderRemoveItemTest {
    private Product mousePad;
    private Product ramMemory;

    private Order order;
    private OrderItemId orderItemId;

    @BeforeEach
    void setUp() {
        mousePad = ProductTestDataBuilder.aProductAltMousePad().build();
        ramMemory = ProductTestDataBuilder.aProductAltRamMemory().build();

        order = Order.draft(new CustomerId());

        order.addItem(mousePad, Quantity.of(1));
        order.addItem(ramMemory, Quantity.of(1));

        order.items().stream()
                .filter(i -> i.productId().equals(ramMemory.id()))
                .findFirst().ifPresent(i -> orderItemId = i.id());
    }

    @Test
    void givenDraftOrder_whenRemoveItem_shouldAllow() {
        order.removeItem(orderItemId);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.items()).hasSize(1),
                o -> Assertions.assertThat(o.totalItems()).isEqualTo(Quantity.of(1)),
                o -> Assertions.assertThat(o.totalAmount()).isEqualTo(mousePad.price())
        );
    }

    @Test
    void givenDraftOrder_whenTryToRemoveInexistentItem_shouldNotAllow() {
        orderItemId = new OrderItemId();

        Assertions.assertThatThrownBy(() -> order.removeItem(orderItemId)).isInstanceOf(OrderDoesNotContainItemException.class);
    }

    @Test
    void givenPlacedOrder_whenTryToRemoveItem_shouldNotAllow() {
        order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        Assertions.assertThatThrownBy(() -> order.removeItem(orderItemId)).isInstanceOf(OrderCannotBeEditedException.class);
    }
}