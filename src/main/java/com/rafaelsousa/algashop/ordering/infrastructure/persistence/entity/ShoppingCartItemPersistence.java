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
@Table(name = "shopping_cart_item")
public class ShoppingCartItemPersistence extends AuditableEntity {

    @Id
    private UUID id;

    private UUID productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Boolean available;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private ShoppingCartPersistence shoppingCart;

    public UUID getShoppingCardId() {
        if (Objects.isNull(shoppingCart)) return null;

        return this.shoppingCart.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartItemPersistence that = (ShoppingCartItemPersistence) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}