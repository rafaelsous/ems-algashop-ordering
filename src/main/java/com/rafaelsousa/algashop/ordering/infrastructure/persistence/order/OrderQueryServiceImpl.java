package com.rafaelsousa.algashop.ordering.infrastructure.persistence.order;

import com.rafaelsousa.algashop.ordering.application.order.OrderQueryService;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.application.utility.Mapper;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {
    private final OrderPersistenceRepository orderPersistenceRepository;
    private final Mapper mapper;

    @Override
    public OrderDetailOutput findById(String id) {
        OrderId orderId = new OrderId(id);
        OrderPersistence orderPersistence = orderPersistenceRepository.findById(orderId.value().toLong())
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return mapper.convert(orderPersistence, OrderDetailOutput.class);
    }
}