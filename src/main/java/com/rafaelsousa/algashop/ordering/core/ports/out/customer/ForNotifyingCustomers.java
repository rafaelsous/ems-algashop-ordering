package com.rafaelsousa.algashop.ordering.core.ports.out.customer;

import lombok.Builder;

import java.util.UUID;

public interface ForNotifyingCustomers {
    void notifyNewRegistration(NotifyNewRegistrationInput notifyNewRegistrationInput);

    @Builder
    record NotifyNewRegistrationInput(UUID rawCustomerId, String firstName, String email) { }
}