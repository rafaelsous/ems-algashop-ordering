package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerQueryService;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerQueryServiceImpl implements CustomerQueryService {
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
    public CustomerOutput findById(UUID rawCustomerId) {
        Objects.requireNonNull(rawCustomerId);

        try {
            return entityManager.createQuery(FIND_BY_ID_AS_OUTPUT_JPQL, CustomerOutput.class)
                    .setParameter("id", rawCustomerId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new CustomerNotFoundException(new CustomerId(rawCustomerId));
        }
    }
}