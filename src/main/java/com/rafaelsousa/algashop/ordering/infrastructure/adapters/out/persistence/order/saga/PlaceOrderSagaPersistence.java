package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order.saga;

import com.rafaelsousa.algashop.ordering.core.application.order.saga.PlaceOrderSagaStep;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.saga.SagaInstancePersistence;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor
@ToString(callSuper = true, of = "step")
@DiscriminatorValue(PlaceOrderSagaPersistence.SAGA_TYPE)
public class PlaceOrderSagaPersistence extends SagaInstancePersistence {

	public static final String SAGA_TYPE = "PLACE_ORDER";

	@Enumerated(EnumType.STRING)
	private PlaceOrderSagaStep step;
}
