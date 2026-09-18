package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessedMessageCleaner {
	private static final int BATCH_SIZE = 200;
	private static final int MAX_BATCHES = 20;
	private static final Duration RETENTION = Duration.ofDays(10);

	private final TransactionTemplate transactionTemplate;
	private final ProcessedMessageRepository processedMessageRepository;

	@Scheduled(cron = "0 */10 * * * *") // SÓ PARA O TESTE: a cada minuto
	@SchedulerLock(name = "processedMessageCleanup")
	public void purgeExpired() {
		OffsetDateTime threshold = OffsetDateTime.now().minus(RETENTION);

		log.info("Starting purge of expired processed messages, threshold={}", threshold);

		int totalDeleted = 0;
		for (int i = 0; i < MAX_BATCHES; i++) {
			Integer batchDeleted = transactionTemplate.execute(_ ->
				processedMessageRepository.deleteBatchOlderThan(threshold, BATCH_SIZE));
			totalDeleted += batchDeleted == null ? 0 : batchDeleted;

			if (batchDeleted == null || batchDeleted < BATCH_SIZE) {
				break;
			}
		}

		log.info("Finished purge of expired processed messages. Total deleted: {}", totalDeleted);
	}
}
