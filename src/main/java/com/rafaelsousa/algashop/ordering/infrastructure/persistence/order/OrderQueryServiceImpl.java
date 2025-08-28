package com.rafaelsousa.algashop.ordering.infrastructure.persistence.order;

import com.rafaelsousa.algashop.ordering.application.order.query.CustomerMinimalOutput;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderQueryService;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.rafaelsousa.algashop.ordering.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.application.utility.PageFilter;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {
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
    public Page<OrderSummaryOutput> filter(PageFilter filter) {
        Long totalQueryResults = countTotalQueryResults(filter);

        if (totalQueryResults.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }

        return filterQuery(filter, totalQueryResults);
    }

    private Long countTotalQueryResults(PageFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<OrderPersistence> root = criteriaQuery.from(OrderPersistence.class);

        Expression<Long> count = criteriaBuilder.count(root);
        criteriaQuery.select(count);

        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);

        return typedQuery.getSingleResult();
    }

    private Page<OrderSummaryOutput> filterQuery(PageFilter filter, Long totalQueryResults) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderSummaryOutput> criteriaQuery = criteriaBuilder.createQuery(OrderSummaryOutput.class);
        Root<OrderPersistence> root = criteriaQuery.from(OrderPersistence.class);

        Path<Object> customer = root.get("customer");

        criteriaQuery.select(criteriaBuilder.construct(OrderSummaryOutput.class,
                        root.get("id"),
                        criteriaBuilder.construct(CustomerMinimalOutput.class,
                                customer.get("id"),
                                customer.get("firstName"),
                                customer.get("lastName"),
                                customer.get("document"),
                                customer.get("email"),
                                customer.get("phone")
                        ),
                        root.get("totalItems"),
                        root.get("totalAmount"),
                        root.get("placedAt"),
                        root.get("paidAt"),
                        root.get("canceledAt"),
                        root.get("readyAt"),
                        root.get("status"),
                        root.get("paymentMethod")
                )
        );

        TypedQuery<OrderSummaryOutput> typedQuery = entityManager.createQuery(criteriaQuery);

        typedQuery.setFirstResult(filter.getPage() * filter.getSize());
        typedQuery.setMaxResults(filter.getSize());

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(typedQuery.getResultList(), pageRequest, totalQueryResults);
    }
}