package com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "id")
@Entity
@Table(name = "order_item")
public class OrderItemPersistence {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private UUID productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private OrderPersistence order;

    public Long getOrderId() {
        if (Objects.isNull(getOrder())) return null;

        return getOrder().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemPersistence that = (OrderItemPersistence) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}