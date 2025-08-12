package com.rafaelsousa.algashop.ordering.domain.model.repository;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;

public interface Orders extends Repository<Order, OrderId> {
}