package com.rafaelsousa.algashop.ordering.core.application.order.query;

import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.CustomerMinimalOutput;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderSummaryOutput;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderSummaryOutputTestDataBuilder {

    public static OrderSummaryOutput.OrderSummaryOutputBuilder placedOrder() {
        return placedOrder(new OrderId().toString());
    }

    public static OrderSummaryOutput.OrderSummaryOutputBuilder placedOrder(String orderId) {
        return OrderSummaryOutput.builder()
                .id(orderId)
                .customer(CustomerMinimalOutput.builder()
                        .id(new CustomerId().value())
                        .firstName("John")
                        .lastName("Doe")
                        .document("12345")
                        .email("john.doe@email.com")
                        .phone("1191234564")
                        .build())
                .totalItems(2)
                .totalAmount(BigDecimal.valueOf(41.98))
                .placedAt(OffsetDateTime.now())
                .paidAt(null)
                .canceledAt(null)
                .readyAt(null)
                .status("PLACED")
                .paymentMethod("GATEWAY_BALANCE");
    }
}