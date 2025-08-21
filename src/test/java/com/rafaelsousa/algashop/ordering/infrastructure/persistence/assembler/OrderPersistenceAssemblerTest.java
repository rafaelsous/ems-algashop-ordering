package com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler;

import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderItem;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceAssemblerTest {

    @Mock
    private CustomerPersistenceRepository customerPersistenceRepository;

    @InjectMocks
    private OrderPersistenceAssembler assembler;

    @BeforeEach
    void setUp() {
        when(customerPersistenceRepository.getReferenceById(any(UUID.class)))
                .then(a -> {
                    UUID customerId = a.getArgument(0, UUID.class);

                    return CustomerPersistenceTestDataBuilder
                            .aCustomer().id(customerId).build();
                });
    }

    @Test
    void shouldConvertFromDomain() {
        Order order = OrderTestDataBuilder.anOrder().build();
        OrderPersistence orderPersistence = assembler.fromDomain(order);

        assertThat(orderPersistence).satisfies(
                op -> assertThat(op.getId()).isEqualTo(order.id().value().toLong()),
                op -> assertThat(op.getCustomerId()).isEqualTo(order.customerId().value()),
                op -> assertThat(op.getTotalAmount()).isEqualTo(order.totalAmount().value()),
                op -> assertThat(op.getTotalItems()).isEqualTo(order.totalItems().value()),
                op -> assertThat(op.getStatus()).isEqualTo(order.status().name()),
                op -> assertThat(op.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
                op -> assertThat(op.getPlacedAt()).isEqualTo(order.placedAt()),
                op -> assertThat(op.getPaidAt()).isEqualTo(order.paidAt()),
                op -> assertThat(op.getCanceledAt()).isEqualTo(order.canceledAt()),
                op -> assertThat(op.getReadyAt()).isEqualTo(order.readyAt())
        );
    }

    @Test
    void givenOrderWithNotItems_whenCallMerge_shouldeRemovePersistenceItems() {
        Order order = OrderTestDataBuilder.anOrder().withItems(false).build();
        OrderPersistence orderPersistence = OrderPersistenceTestDataBuilder.existingOrder().build();

        assertThat(order.items()).isEmpty();
        assertThat(orderPersistence.getItems()).isNotEmpty();

        orderPersistence = assembler.merge(orderPersistence, order);

        assertThat(orderPersistence.getItems()).isEmpty();
    }

    @Test
    void givenOrderWithItems_whenCallMerge_shouldAddItemToPersistence() {
        Order order = OrderTestDataBuilder.anOrder()
                .withItems(true)
                .build();
        OrderPersistence orderPersistence = OrderPersistenceTestDataBuilder.existingOrder()
                .items(new HashSet<>())
                .build();

        assertThat(order.items()).isNotEmpty();
        assertThat(orderPersistence.getItems()).isEmpty();

        assembler.merge(orderPersistence, order);

        assertThat(orderPersistence.getItems()).isNotEmpty();
        assertThat(orderPersistence.getItems()).hasSize(order.items().size());
    }

    @Test
    void givenOrderWithItems_whenCallMerge_shouldRemoveMergeCorrectly() {
        Order order = OrderTestDataBuilder.anOrder().build();

        assertThat(order.items()).hasSize(2);

        Set<OrderItemPersistence> items = order.items().stream()
                .map(assembler::fromDomain)
                .collect(Collectors.toSet());

        OrderPersistence orderPersistence = OrderPersistenceTestDataBuilder.existingOrder()
                .items(items)
                .build();

        OrderItem orderItem = order.items().iterator().next();
        order.removeItem(orderItem.id());

        assembler.merge(orderPersistence, order);

        assertThat(orderPersistence.getItems()).isNotEmpty();
        assertThat(orderPersistence.getItems()).hasSize(order.items().size());
    }
}