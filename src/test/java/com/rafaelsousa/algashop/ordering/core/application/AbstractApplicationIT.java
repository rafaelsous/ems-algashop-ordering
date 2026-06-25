package com.rafaelsousa.algashop.ordering.core.application;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityCheckApplicationService;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.config.MockJwtDecoderConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.config.TestcontainerPostgreSQLConfig;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Import({TestcontainerPostgreSQLConfig.class, MockJwtDecoderConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractApplicationIT {

	@MockitoBean
	protected SecurityCheckApplicationService securityCheckApplicationService;

	@BeforeEach
	public void preSetup() {
		Mockito.when(securityCheckApplicationService.isCustomer()).thenReturn(true);
		Mockito.when(securityCheckApplicationService.getAuthenticatedUserId())
			.thenReturn(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value());
	}
}