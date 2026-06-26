package com.rafaelsousa.algashop.ordering.core.application.security;

import com.rafaelsousa.algashop.ordering.core.application.AbstractApplicationIT;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.utils.WithMockJwt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityChecksIT extends AbstractApplicationIT {

	@Autowired
	private SecurityChecks securityChecks;

	@Test
    void givenAuthenticatedCustomerShouldAllowOrderForHimself() {
		UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();
		boolean canOrderFor = securityChecks.canOrderFor(customerId);

		assertThat(canOrderFor).isTrue();
	}

	@Test
	@WithMockJwt(role = "", audiences = "machine-client-id", subject = "machine-client-id")
	void givenAuthenticatedMachineShouldNotAllowOrder() {
		UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();
		boolean canOrderFor = securityChecks.canOrderFor(customerId);

		assertThat(canOrderFor).isFalse();
	}

	@Test
	@WithMockJwt(role = "", audiences = "machine-client-id", subject = "machine-client-id")
	void givenAuthenticatedMachineShouldReturnTrue() {
        boolean isMachineAuthenticated = securityChecks.isMachineAuthenticated();

		assertThat(isMachineAuthenticated).isTrue();
	}
}