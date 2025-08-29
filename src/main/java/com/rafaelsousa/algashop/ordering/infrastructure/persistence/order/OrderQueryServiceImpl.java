package com.rafaelsousa.algashop.ordering.infrastructure.persistence.order;

import com.rafaelsousa.algashop.ordering.application.order.query.*;
import com.rafaelsousa.algashop.ordering.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderNotFoundException;
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
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {
    private static final String TOTAL_AMOUNT_ATTRIBUTE = "totalAmount";
    private static final String PLACED_AT_ATTRIBUTE = "placedAt";

    private final OrderPersistenceRepository orderPersistenceRepository;
    private final Mapper mapper;
    private final EntityManager entityManager;

    @Override
    public OrderDetailOutput findById(String id) {
        OrderId orderId = new OrderId(id);
        OrderPersistence orderPersistence = orderPersistenceRepository.findById(orderId.value().toLong())
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return mapper.convert(orderPersistence, OrderDetailOutput.class);
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        Long totalQueryResults = countTotalQueryResults(filter);

        if (totalQueryResults.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }

        return filterQuery(filter, totalQueryResults);
    }

    private Long countTotalQueryResults(OrderFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<OrderPersistence> root = criteriaQuery.from(OrderPersistence.class);

        Expression<Long> count = criteriaBuilder.count(root);
        Predicate[] predicates = toPredicates(criteriaBuilder, root, filter);

        criteriaQuery.select(count);
        criteriaQuery.where(predicates);

        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getSingleResult();
    }

    private Page<OrderSummaryOutput> filterQuery(OrderFilter filter, Long totalQueryResults) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderSummaryOutput> criteriaQuery = criteriaBuilder.createQuery(OrderSummaryOutput.class);
        Root<OrderPersistence> root = criteriaQuery.from(OrderPersistence.class);

        Path<Object> customer = root.get("customer");

        criteriaQuery.select(criteriaBuilder.construct(OrderSummaryOutput.class,
                        root.get("id"),
                        root.get("totalItems"),
                        root.get(TOTAL_AMOUNT_ATTRIBUTE),
                        root.get(PLACED_AT_ATTRIBUTE),
                        root.get("paidAt"),
                        root.get("canceledAt"),
                        root.get("readyAt"),
                        root.get("status"),
                        root.get("paymentMethod"),
                        criteriaBuilder.construct(CustomerMinimalOutput.class,
                                customer.get("id"),
                                customer.get("firstName"),
                                customer.get("lastName"),
                                customer.get("document"),
                                customer.get("email"),
                                customer.get("phone")
                        )
                )
        );

        Predicate[] predicates = toPredicates(criteriaBuilder, root, filter);
        Order sortOrder = toSortOrder(criteriaBuilder, root, filter);

        criteriaQuery.where(predicates);

        if (Objects.nonNull(sortOrder)) {
            criteriaQuery.orderBy(sortOrder);
        }

        TypedQuery<OrderSummaryOutput> typedQuery = entityManager.createQuery(criteriaQuery);

        typedQuery.setFirstResult(filter.getPage() * filter.getSize());
        typedQuery.setMaxResults(filter.getSize());

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(typedQuery.getResultList(), pageRequest, totalQueryResults);
    }

    private Order toSortOrder(CriteriaBuilder criteriaBuilder, Root<OrderPersistence> root, OrderFilter filter) {
        if (filter.getSortDirectionOrDefault() == Sort.Direction.ASC) {
            return criteriaBuilder.asc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }

        if (filter.getSortDirectionOrDefault() == Sort.Direction.DESC) {
            return criteriaBuilder.desc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }

        return null;
    }

    private Predicate[] toPredicates(CriteriaBuilder criteriaBuilder, Root<OrderPersistence> root, OrderFilter filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(filter.getCustomerId())) {
            predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), filter.getCustomerId()));
        }

        if (Objects.nonNull(filter.getStatus()) && StringUtils.hasText(filter.getStatus())) {
            predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus().toUpperCase()));
        }

        if (Objects.nonNull(filter.getOrderId()) && StringUtils.hasText(filter.getOrderId())) {
            long orderIdLongValue;

            try {
                OrderId orderId = new OrderId(filter.getOrderId());
                orderIdLongValue = orderId.value().toLong();
            } catch (IllegalArgumentException ex) {
                orderIdLongValue = 0L;
            }

            predicates.add(criteriaBuilder.equal(root.get("id"), orderIdLongValue));
        }

        if (Objects.nonNull(filter.getPlacedAtFrom())) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(PLACED_AT_ATTRIBUTE), filter.getPlacedAtFrom()));
        }

        if (Objects.nonNull(filter.getPlacedAtTo())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(PLACED_AT_ATTRIBUTE), filter.getPlacedAtTo()));
        }

        if (Objects.nonNull(filter.getTotalAmountFrom())) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(TOTAL_AMOUNT_ATTRIBUTE), filter.getTotalAmountFrom()));
        }

        if (Objects.nonNull(filter.getTotalAmountTo())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(TOTAL_AMOUNT_ATTRIBUTE), filter.getTotalAmountTo()));
        }

        return predicates.toArray(new Predicate[0]);
    }
}