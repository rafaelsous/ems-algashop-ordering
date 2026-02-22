package com.rafaelsousa.algashop.ordering.core.application;

import com.rafaelsousa.algashop.ordering.infrastructure.config.TestcontainerPostgreSQLConfig;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Import(TestcontainerPostgreSQLConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractApplicationIT {

}