package com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.CustomerPersistence;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceDisassembler {

    public Customer toDomain(CustomerPersistence customerPersistence) {
        return Customer.existing()
                .id(new CustomerId(customerPersistence.getId()))
                .fullName(FullName.of(customerPersistence.getFirstName(), customerPersistence.getLastName()))
                .birthDate(BirthDate.of(customerPersistence.getBirthDate()))
                .email(Email.of(customerPersistence.getEmail()))
                .phone(Phone.of(customerPersistence.getPhone()))
                .document(Document.of(customerPersistence.getDocument()))
                .promotionNotificationsAllowed(customerPersistence.getPromotionNotificationsAllowed())
                .archived(customerPersistence.getArchived())
                .registeredAt(customerPersistence.getRegisteredAt())
                .archivedAt(customerPersistence.getArchivedAt())
                .loyaltyPoints(new LoyaltyPoints(customerPersistence.getLoyaltyPoints()))
                .address(
                        AddressEmbeddableDisassembler.toDomain(customerPersistence.getAddress())
                )
                .version(customerPersistence.getVersion())
                .build();
    }
}