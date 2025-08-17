package com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "id")
@Entity
@Table(name = "customer")
@EntityListeners(AuditingEntityListener.class)
public class CustomerPersistence {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String document;
    private Boolean promotionNotificationsAllowed;
    private Boolean archived;
    private OffsetDateTime registeredAt;
    private OffsetDateTime archivedAt;
    private Integer loyaltyPoints;
    private AddressEmbeddable address;

    @CreatedBy
    private UUID createdByUserId;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CustomerPersistence that = (CustomerPersistence) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}