package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Document;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Email;
import com.rafaelsousa.algashop.ordering.domain.model.commons.FullName;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Phone;
import com.rafaelsousa.algashop.ordering.domain.model.customer.BirthDate;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddableDisassembler;
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