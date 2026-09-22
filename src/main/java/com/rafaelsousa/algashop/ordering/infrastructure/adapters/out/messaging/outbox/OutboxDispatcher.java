package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.outbox.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.messaging.outbox.dispatcher.enabled", havingValue = "true")
public class OutboxDispatcher {
    private final OutboxKafkaSender outboxKafkaSender;
    private final OutboxProperties outboxProperties;
    private final TransactionTemplate transactionTemplate;
    private final OutboxMessageRepository outboxMessageRepository;

    @SchedulerLock(name = "outbox-dispatcher", lockAtMostFor = "PT5M")
    @Scheduled(fixedDelayString = "${algashop.messaging.outbox.poll-interval}")
    public void dispatch() {
        OffsetDateTime deadLine = OffsetDateTime.now().plus(outboxProperties.getBatchDeadLine());

        List<OutboxMessage> batch =
                outboxMessageRepository.findBatch(
                        PageRequest.of(0, outboxProperties.getBatchSize()));

        if (!batch.isEmpty()) {
            for (OutboxMessage message : batch) {
                if (OffsetDateTime.now().isAfter(deadLine)) {
                    log.warn(
                            "Outbox dispatcher reached deadline of {}. Stopping processing batch.",
                            outboxProperties.getBatchDeadLine());
                    break;
                }

                if (!isEligible(message)) continue;

                try {
                    outboxKafkaSender.send(message);
                    transactionTemplate.executeWithoutResult(
                            _ -> outboxMessageRepository.deleteMessage(message.getId()));
                } catch (Exception ex) {
                    transactionTemplate.executeWithoutResult(_ -> registerFailed(message, ex));
                }
            }
        }
    }

    private void registerFailed(OutboxMessage message, Exception ex) {
        int attempts = message.getAttempts() + 1;
        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime failedAt = null;
        if (attempts >= outboxProperties.getMaxAttempts()) {
            failedAt = now;
        }

        OffsetDateTime nextAttemptAt = now.plus(outboxProperties.getBackoff());
        String lastError = extractError(ex);

        outboxMessageRepository.registerFailed(
                message.getId(), attempts, nextAttemptAt, lastError, failedAt);

        if (failedAt != null) {
            log.error(
                    "Permanent failed sending message from outbox {} after {} attempts. Manual intervention required.",
                    message.getId(),
                    attempts);
        }
    }

    private String extractError(Exception ex) {
        String description;
        if (ex.getCause() != null) {
            description = "Error: %s\n Cause:\n %s".formatted(ex.getMessage(), ex.getCause().getMessage());
        } else {
            description = "Error: %s".formatted(ex.getMessage());
        }

        return description;
    }

    private boolean isEligible(OutboxMessage message) {
        return message.getFailedAt() == null
                && !message.getNextAttemptAt().isAfter(OffsetDateTime.now());
    }
}
