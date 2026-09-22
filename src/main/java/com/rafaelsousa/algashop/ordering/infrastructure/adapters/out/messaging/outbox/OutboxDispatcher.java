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
        List<OutboxMessage> batch =
                outboxMessageRepository.findBatch(
                        PageRequest.of(0, outboxProperties.getBatchSize()));

        if (!batch.isEmpty()) {
            for (OutboxMessage message : batch) {
                outboxKafkaSender.send(message);

                transactionTemplate.executeWithoutResult(
                        _ -> outboxMessageRepository.deleteMessage(message.getId()));
            }
        }
    }
}
