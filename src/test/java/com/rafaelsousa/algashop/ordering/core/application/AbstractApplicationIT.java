package com.rafaelsousa.algashop.ordering.core.application;

import com.rafaelsousa.algashop.ordering.infrastructure.config.MockJwtDecoderConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.config.TestcontainerPostgreSQLConfig;
import com.rafaelsousa.algashop.ordering.utils.CacheTestConfig;
import com.rafaelsousa.algashop.ordering.utils.WithMockJwt;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@WithMockJwt
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainerPostgreSQLConfig.class, MockJwtDecoderConfig.class, CacheTestConfig.class})
public abstract class AbstractApplicationIT {}
