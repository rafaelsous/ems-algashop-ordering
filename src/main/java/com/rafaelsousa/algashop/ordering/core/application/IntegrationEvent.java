package com.rafaelsousa.algashop.ordering.core.application;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IntegrationEvent {

	@JsonIgnore
	String getAggregateId();
}
