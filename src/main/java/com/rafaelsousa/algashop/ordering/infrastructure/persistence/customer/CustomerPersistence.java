package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@ToString(of = "id")
@Entity
@Table(name = "customer")
@EntityListeners(AuditingEntityListener.class)
public class CustomerPersistence extends AbstractAggregateRoot<CustomerPersistence> {

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

    @Builder
    public CustomerPersistence(UUID id, String firstName, String lastName, LocalDate birthDate, String email,
                               String phone, String document, Boolean promotionNotificationsAllowed, Boolean archived,
                               OffsetDateTime registeredAt, OffsetDateTime archivedAt, Integer loyaltyPoints,
                               AddressEmbeddable address, UUID createdByUserId, OffsetDateTime lastModifiedAt,
                               UUID lastModifiedByUserId, Long version) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
        this.archived = archived;
        this.registeredAt = registeredAt;
        this.archivedAt = archivedAt;
        this.loyaltyPoints = loyaltyPoints;
        this.address = address;
        this.createdByUserId = createdByUserId;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedByUserId = lastModifiedByUserId;
        this.version = version;
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