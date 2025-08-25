package com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistence;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Getter @Setter
@NoArgsConstructor
@ToString(of = "id")
@Entity
@Table(name = "shopping_cart")
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartPersistence extends AbstractAggregateRoot<ShoppingCartPersistence> {

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

    @CreatedBy
    private UUID createdByUserId;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;

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

    public Collection<Object> getEvents() {
        return super.domainEvents();
    }

    public void addEvents(Collection<Object> events) {
        if (Objects.nonNull(events)) {
            for (Object event : events) {
                this.registerEvent(event);
            }
        }
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