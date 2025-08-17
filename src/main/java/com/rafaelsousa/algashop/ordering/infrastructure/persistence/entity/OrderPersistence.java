package com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Table(name = "\"order\"")
@EntityListeners(AuditingEntityListener.class)
public class OrderPersistence {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn
    private CustomerPersistence customer;

    private BigDecimal totalAmount;
    private Integer totalItems;
    private String status;
    private String paymentMethod;

    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;

    @Embedded
    private BillingEmbeddable billing;

    @Embedded
    private ShippingEmbeddable shipping;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItemPersistence> items = new HashSet<>();

    @CreatedBy
    private UUID createdByUserId;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;

    @Builder
    public OrderPersistence(Long id, CustomerPersistence customer, BigDecimal totalAmount, Integer totalItems, String status,
                            String paymentMethod, OffsetDateTime placedAt, OffsetDateTime paidAt,
                            OffsetDateTime canceledAt, OffsetDateTime readyAt, BillingEmbeddable billing,
                            ShippingEmbeddable shipping, Set<OrderItemPersistence> items, UUID createdByUserId,
                            OffsetDateTime lastModifiedAt, UUID lastModifiedByUserId, Long version) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.placedAt = placedAt;
        this.paidAt = paidAt;
        this.canceledAt = canceledAt;
        this.readyAt = readyAt;
        this.billing = billing;
        this.shipping = shipping;
        this.replaceItems(items);
        this.createdByUserId = createdByUserId;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedByUserId = lastModifiedByUserId;
        this.version = version;
    }

    public void replaceItems(Set<OrderItemPersistence> items) {
        if (Objects.isNull(items) || items.isEmpty()) {
            this.setItems(new HashSet<>());

            return;
        }

        items.forEach(item -> item.setOrder(this));
        this.setItems(items);
    }

    public void addItem(OrderItemPersistence item) {
        if (Objects.isNull(item)) return;

        if (Objects.isNull(this.getItems())) this.setItems(new HashSet<>());

        item.setOrder(this);
        this.getItems().add(item);
    }

    public UUID getCustomerId() {
        if (Objects.isNull(this.customer)) return null;

        return this.customer.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderPersistence that = (OrderPersistence) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}