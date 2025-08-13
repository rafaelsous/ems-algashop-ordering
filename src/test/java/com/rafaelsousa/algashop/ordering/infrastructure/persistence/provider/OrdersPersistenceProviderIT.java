package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceAssembler.class,
        OrderPersistenceDisassembler.class,
        SpringDataAuditingConfig.class
})
class OrdersPersistenceProviderIT {
    private final OrdersPersistenceProvider ordersPersistenceProvider;
    private final OrderPersistenceRepository orderPersistenceRepository;

    @Autowired
    public OrdersPersistenceProviderIT(OrdersPersistenceProvider ordersPersistenceProvider, OrderPersistenceRepository orderPersistenceRepository) {
        this.ordersPersistenceProvider = ordersPersistenceProvider;
        this.orderPersistenceRepository = orderPersistenceRepository;
    }

    @Test
    void shouldUpdateAndKeepPersistenEntityState() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        long orderId = order.id().value().toLong();

        ordersPersistenceProvider.add(order);

        OrderPersistence orderPersistence = orderPersistenceRepository.findById(orderId).orElseThrow();

        assertThat(orderPersistence).satisfies(
                op -> assertThat(op.getStatus()).isEqualTo(OrderStatus.PLACED.name()),

                op -> assertThat(op.getCreatedByUserId()).isNotNull(),
                op -> assertThat(op.getLastModifiedByUserId()).isNotNull(),
                op -> assertThat(op.getLastModifiedAt()).isNotNull()
        );

        order = ordersPersistenceProvider.ofId(order.id()).orElseThrow();
        order.markAsPaid();
        ordersPersistenceProvider.add(order);

        orderPersistence = orderPersistenceRepository.findById(orderId).orElseThrow();

        assertThat(orderPersistence).satisfies(
                op -> assertThat(op.getStatus()).isEqualTo(OrderStatus.PAID.name()),

                op -> assertThat(op.getCreatedByUserId()).isNotNull(),
                op -> assertThat(op.getLastModifiedByUserId()).isNotNull(),
                op -> assertThat(op.getLastModifiedAt()).isNotNull()
        );
    }
}