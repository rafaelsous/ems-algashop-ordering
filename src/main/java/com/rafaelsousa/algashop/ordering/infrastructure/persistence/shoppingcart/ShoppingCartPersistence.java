package com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.commons.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@ToString(of = "id")
@Entity
@Table(name = "shopping_cart")
public class ShoppingCartPersistence extends AuditableEntity {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn
    private CustomerPersistence customer;

    private BigDecimal totalAmount;
    private Integer totalItems;
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
    private Set<ShoppingCartItemPersistence> items = new HashSet<>();

    @Builder(toBuilder = true)
    public ShoppingCartPersistence(UUID id, CustomerPersistence customer, BigDecimal totalAmount, Integer totalItems,
                                   OffsetDateTime createdAt, Set<ShoppingCartItemPersistence> items) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.createdAt = createdAt;
        this.replaceItems(items);
    }

    public void replaceItems(Set<ShoppingCartItemPersistence> updatedItems) {
        if (Objects.isNull(updatedItems) || updatedItems.isEmpty()) {
            this.setItems(new HashSet<>());

            return;
        }

        updatedItems.forEach(item -> item.setShoppingCart(this));
        this.setItems(updatedItems);
    }

    public void addItem(ShoppingCartItemPersistence item) {
        if (Objects.isNull(item)) return;

        if (Objects.isNull(this.getItems())) this.setItems(new HashSet<>());

        item.setShoppingCart(this);
        this.getItems().add(item);
    }

    public UUID getCustomerId() {
        if (getCustomer() == null) return null;

        return this.customer.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartPersistence that = (ShoppingCartPersistence) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}