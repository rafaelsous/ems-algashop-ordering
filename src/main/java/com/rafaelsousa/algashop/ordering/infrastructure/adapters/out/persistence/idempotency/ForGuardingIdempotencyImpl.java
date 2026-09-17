package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.idempotency;

import com.rafaelsousa.algashop.ordering.core.ports.out.idempotency.ForGuardingIdempotency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForGuardingIdempotencyImpl implements ForGuardingIdempotency {
	private final ProcessedMessageRepository processedMessageRepository;
	private final TransactionTemplate transactionTemplate;

	@Override
	public boolean runOnce(UUID idempotencyKey, Runnable work) {
		return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
			try {
				processedMessageRepository.saveAndFlush(new ProcessedMessage(idempotencyKey));
			} catch (DataIntegrityViolationException ex) {
				status.setRollbackOnly();
				log.warn("Duplicate execution skipped: idempotencyKey={}", idempotencyKey);

				return false;
			}

			// Execute the work
			work.run();

			return true;
		}));
	}
}
