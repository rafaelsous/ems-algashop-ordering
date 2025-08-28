package com.rafaelsousa.algashop.ordering.application.order;

import com.rafaelsousa.algashop.ordering.application.order.query.OrderDetailOutput;

public interface OrderQueryService {
    OrderDetailOutput findById(String id);
}