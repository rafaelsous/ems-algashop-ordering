package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.valueobject.ProductName;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.ProductId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void shouldCreateOrderItem() {
        // Arrange
        var orderId = new OrderId();
        var productId = new ProductId();
        var productName = ProductName.of("Product 1");
        var price = Money.of("10.0");
        var quantity = Quantity.of(1);

        // Act
        var orderItem = OrderItem.brandNew()
            .orderId(orderId)
            .productId(productId)
            .productName(productName)
            .price(price)
            .quantity(quantity)
            .build();

        // Assert
        Assertions.assertEquals(productId, orderItem.productId());
        Assertions.assertEquals(productName, orderItem.productName());
        Assertions.assertEquals(quantity, orderItem.quantity());
        Assertions.assertEquals(price, orderItem.price());
    }
}