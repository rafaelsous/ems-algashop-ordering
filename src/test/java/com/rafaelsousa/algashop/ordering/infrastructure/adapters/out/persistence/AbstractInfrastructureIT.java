package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityChecks;
import com.rafaelsousa.algashop.ordering.infrastructure.config.MockJwtDecoderConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.config.TestcontainerPostgreSQLConfig;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainerPostgreSQLConfig.class, MockJwtDecoderConfig.class})
public abstract class AbstractInfrastructureIT {

	@MockitoBean
	protected SecurityChecks securityChecks;

	@BeforeEach
	public void setup() {
		Mockito.when(securityChecks.isAuthenticated()).thenReturn(true);
		Mockito.when(securityChecks.isMachineAuthenticated()).thenReturn(false);
		Mockito.when(securityChecks.getAuthenticatedUserId()).thenReturn(UUID.randomUUID());
	}
}