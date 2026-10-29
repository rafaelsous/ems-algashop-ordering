package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order.saga;

import com.rafaelsousa.algashop.ordering.core.application.order.saga.PlaceOrderSaga;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.saga.ForStoringPlaceOrderSagas;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceOrderSagasPersistenceProvider implements ForStoringPlaceOrderSagas {
    private final PlaceOrderSagaPersistenceRepository placeOrderSagaPersistenceRepository;

    @Override
    public Optional<PlaceOrderSaga> ofId(UUID sagaId) {
        return placeOrderSagaPersistenceRepository.findById(sagaId).map(this::toSaga);
    }

    @Override
    @Transactional
    public void add(PlaceOrderSaga saga) {
        placeOrderSagaPersistenceRepository.saveAndFlush(toPersistence(saga));
    }

    private PlaceOrderSaga toSaga(PlaceOrderSagaPersistence placeOrderSagaPersistence) {
        return PlaceOrderSaga.existing(
                placeOrderSagaPersistence.getSagaId(),
                new OrderId(placeOrderSagaPersistence.getAggregateId()),
                placeOrderSagaPersistence.getStatus(),
                placeOrderSagaPersistence.getStep(),
                placeOrderSagaPersistence.getVersion());
    }

    private PlaceOrderSagaPersistence toPersistence(PlaceOrderSaga saga) {
        PlaceOrderSagaPersistence persistence = new PlaceOrderSagaPersistence();
        persistence.setSagaId(saga.sagaId());
        persistence.setAggregateId(saga.orderId().toString());
        persistence.setStatus(saga.status());
        persistence.setStep(saga.step());
        persistence.setVersion(saga.version());

        return persistence;
    }
}
