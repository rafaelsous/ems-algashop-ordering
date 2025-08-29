package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerFilter;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerQueryService;
import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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

    @Override
    public Page<CustomerSummaryOutput> filter(CustomerFilter filter) {
        Long totalQueryResults = countTotalQueryResults(filter);

        if (totalQueryResults.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }

        return filterQuery(filter, totalQueryResults);
    }

    private Long countTotalQueryResults(CustomerFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<CustomerPersistence> root = criteriaQuery.from(CustomerPersistence.class);

        Expression<Long> count = criteriaBuilder.count(root);
        Predicate[] predicates = toPredicates(criteriaBuilder, root, filter);

        criteriaQuery.select(count);
        criteriaQuery.where(predicates);

        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getSingleResult();
    }

    private Page<CustomerSummaryOutput> filterQuery(CustomerFilter filter, Long totalQueryResults) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerSummaryOutput> criteriaQuery = criteriaBuilder.createQuery(CustomerSummaryOutput.class);
        Root<CustomerPersistence> root = criteriaQuery.from(CustomerPersistence.class);

        criteriaQuery.select(criteriaBuilder.construct(CustomerSummaryOutput.class,
                        root.get("id"),
                        root.get("firstName"),
                        root.get("lastName"),
                        root.get("email"),
                        root.get("document"),
                        root.get("phone"),
                        root.get("birthDate"),
                        root.get("loyaltyPoints"),
                        root.get("registeredAt"),
                        root.get("archivedAt"),
                        root.get("promotionNotificationsAllowed"),
                        root.get("archived")
                )
        );

        Predicate[] predicates = toPredicates(criteriaBuilder, root, filter);
        Order sortOrder = toSortOrder(criteriaBuilder, root, filter);

        criteriaQuery.where(predicates);

        if (Objects.nonNull(sortOrder)) {
            criteriaQuery.orderBy(sortOrder);
        }

        TypedQuery<CustomerSummaryOutput> typedQuery = entityManager.createQuery(criteriaQuery);

        typedQuery.setFirstResult(filter.getPage() * filter.getSize());
        typedQuery.setMaxResults(filter.getSize());

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(typedQuery.getResultList(), pageRequest, totalQueryResults);
    }

    private Order toSortOrder(CriteriaBuilder criteriaBuilder, Root<CustomerPersistence> root, CustomerFilter filter) {
        Order order = null;

        if (filter.getSortDirectionOrDefault() == Sort.Direction.ASC)
            order = criteriaBuilder.asc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));

        if (filter.getSortDirectionOrDefault() == Sort.Direction.DESC)
            order = criteriaBuilder.desc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));

        return order;
    }

    private Predicate[] toPredicates(CriteriaBuilder criteriaBuilder, Root<CustomerPersistence> root, CustomerFilter filter) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(filter.getEmail()) && StringUtils.hasText(filter.getEmail())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
        }

        if (Objects.nonNull(filter.getFirstName()) && StringUtils.hasText(filter.getFirstName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + filter.getFirstName().toLowerCase() + "%"));
        }

        return predicates.toArray(new Predicate[0]);
    }
}