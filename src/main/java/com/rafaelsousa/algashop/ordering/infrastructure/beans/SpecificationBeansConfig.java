package com.rafaelsousa.algashop.ordering.infrastructure.beans;

import com.rafaelsousa.algashop.ordering.domain.model.order.CustomerHaveFreeShippingSpecification;
import com.rafaelsousa.algashop.ordering.domain.model.order.Orders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpecificationBeansConfig {

    @Bean
    public CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification(
            Orders orders,
            @Value("${algashop.shipping.have-free-shipping.rule1.min-points}") int minPointsForFreeShippingRule1,
            @Value("${algashop.shipping.have-free-shipping.rule1.min-sales-quantity}") long minSalesQuantityForFreeShippingRule1,
            @Value("${algashop.shipping.have-free-shipping.rule2.min-points}") int minPointsForFreeShippingRule2
    ) {
        return new CustomerHaveFreeShippingSpecification(
                orders,
                minPointsForFreeShippingRule1,
                minSalesQuantityForFreeShippingRule1,
                minPointsForFreeShippingRule2
        );
    }
}