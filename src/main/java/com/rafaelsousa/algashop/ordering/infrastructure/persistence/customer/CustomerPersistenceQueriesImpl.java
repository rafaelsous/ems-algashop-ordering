package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomerPersistenceQueriesImpl implements CustomerPersistenceQueries {
    private final EntityManager entityManager;

    private static final String FIND_BY_ID_AS_OUTPUT_JPQL = """
            SELECT new com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput(
                c.id,
                c.firstName,
                c.lastName,
                c.email,
                c.document,
                c.phone,
                c.birthDate,
                c.loyaltyPoints,
                c.registeredAt,
                c.archivedAt,
                c.promotionNotificationsAllowed,
                c.archived,
                new com.rafaelsousa.algashop.ordering.application.commons.AddressData(
                    c.address.street,
                    c.address.number,
                    c.address.complement,
                    c.address.neighborhood,
                    c.address.city,
                    c.address.state,
                    c.address.zipCode
                )
            )
            FROM CustomerPersistence c
            WHERE c.id = :id""";

    @Override
    public Optional<CustomerOutput> findByIdAsOutput(UUID rawCustomerId) {
        try {
            CustomerOutput customerOutput = entityManager.createQuery(FIND_BY_ID_AS_OUTPUT_JPQL, CustomerOutput.class)
                    .setParameter("id", rawCustomerId)
                    .getSingleResult();

            return Optional.of(customerOutput);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}