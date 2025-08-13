package com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "\"order\"")
@EntityListeners(AuditingEntityListener.class)
public class OrderPersistence {

    @Id
    @EqualsAndHashCode.Include
    private Long id;
    private UUID customerId;

    private BigDecimal totalAmount;
    private Integer totalItems;
    private String status;
    private String paymentMethod;

    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;

    @CreatedBy
    private UUID createdByUserId;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;
}