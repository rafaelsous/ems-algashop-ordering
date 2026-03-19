package com.rafaelsousa.algashop.ordering.core.domain.model.product;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.web.product.client.http.ProductCatalogApiClient;
import com.rafaelsousa.algashop.ordering.infrastructure.config.TestcontainerPostgreSQLConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@Import(TestcontainerPostgreSQLConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ProductCatalogServiceIT {

	@Autowired
	private ProductCatalogService productCatalogService;

	@MockitoBean
	private ProductCatalogApiClient productCatalogApiClient;

	@Test
	void concurrency() throws InterruptedException {
		UUID rawProductId = UUID.randomUUID();
		ProductId productId = new ProductId(rawProductId);

		when(productCatalogApiClient.getById(rawProductId)).thenReturn(null);

		try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
			executorService.submit(() -> productCatalogService.ofId(productId));
			executorService.submit(() -> productCatalogService.ofId(productId));
			executorService.submit(() -> productCatalogService.ofId(productId));
			executorService.submit(() -> productCatalogService.ofId(productId));
			executorService.submit(() -> productCatalogService.ofId(productId));
			executorService.submit(() -> productCatalogService.ofId(productId));

			boolean awaitTermination = executorService.awaitTermination(30, TimeUnit.SECONDS);

			if (!awaitTermination) {
				executorService.shutdown();
			}

			assertThat(awaitTermination).isFalse();
			assertThat(executorService.isTerminated()).isTrue();
		}
	}
}