package com.rafaelsousa.algashop.ordering.core.application.order;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityChecks;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.ForQueryingOrders;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.core.ports.in.order.OrderFilter;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.OrderSummaryOutput;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.ForObtainingOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderQueryService implements ForQueryingOrders {
    private final ForObtainingOrders forObtainingOrders;
    private final SecurityChecks securityChecks;

    @Override
    public OrderDetailOutput findById(String id) {
        OrderDetailOutput order = forObtainingOrders.findById(id);

        if (!canAccess(order)) {
            throw new AccessDeniedException("You don't have permission to access this order");
        }

        return order;
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        if (securityChecks.isCustomer()) {
            filter.setCustomerId(securityChecks.getAuthenticatedUserId());
        }

        return forObtainingOrders.filter(filter);
    }

    private boolean canAccess(OrderDetailOutput order) {
        if (!securityChecks.isCustomer() && securityChecks.isAuthenticated()) {
            return true;
        }

        return securityChecks.isCustomer()
            && securityChecks.getAuthenticatedUserId().equals(order.getCustomer().getId());
    }
}