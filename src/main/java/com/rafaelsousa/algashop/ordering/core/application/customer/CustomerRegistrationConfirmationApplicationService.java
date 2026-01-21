package com.rafaelsousa.algashop.ordering.core.application.customer;

import com.rafaelsousa.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import com.rafaelsousa.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import com.rafaelsousa.algashop.ordering.core.ports.out.customer.ForObtainingCustomers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationConfirmationApplicationService implements ForConfirmCustomerRegistration {
    private final ForNotifyingCustomers forNotifyingCustomers;
    private final ForObtainingCustomers forObtainingCustomers;

    @Override
    public void confirm(UUID customerId) {
        CustomerOutput customer = forObtainingCustomers.findById(customerId);
        forNotifyingCustomers.notifyNewRegistration(ForNotifyingCustomers.NotifyNewRegistrationInput.builder()
                .rawCustomerId(customerId)
                .firstName(customer.getFirstName())
                .email(customer.getEmail())
                .build());
    }
}