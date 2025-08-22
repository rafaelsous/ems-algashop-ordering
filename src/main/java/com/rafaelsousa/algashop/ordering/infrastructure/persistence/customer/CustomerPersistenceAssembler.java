package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddableAssembler;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomerPersistenceAssembler {

    public CustomerPersistence fromDomain(Customer aggregateRoot) {
        return this.merge(new CustomerPersistence(), aggregateRoot);
    }

    public CustomerPersistence merge(CustomerPersistence customerPersistence, Customer aggregateRoot) {
        customerPersistence.setId(aggregateRoot.id().value());
        customerPersistence.setFirstName(aggregateRoot.fullName().firstName());
        customerPersistence.setLastName(aggregateRoot.fullName().lastName());
        customerPersistence.setBirthDate(Objects.nonNull(aggregateRoot.birthDate()) ? aggregateRoot.birthDate().value() : null);
        customerPersistence.setEmail(aggregateRoot.email().value());
        customerPersistence.setPhone(aggregateRoot.phone().value());
        customerPersistence.setDocument(aggregateRoot.document().value());
        customerPersistence.setPromotionNotificationsAllowed(aggregateRoot.isPromotionNotificationsAllowed());
        customerPersistence.setArchived(aggregateRoot.isArchived());
        customerPersistence.setRegisteredAt(aggregateRoot.registeredAt());
        customerPersistence.setArchivedAt(aggregateRoot.archivedAt());
        customerPersistence.setLoyaltyPoints(aggregateRoot.loyaltyPoints().value());
        customerPersistence.setAddress(
                AddressEmbeddableAssembler.fromPersistence(aggregateRoot.address())
        );
        customerPersistence.setVersion(aggregateRoot.version());

        return customerPersistence;
    }
}