package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.outbox.OutboxMessageRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "true")
public class OutboxRecorder {
	static final String TYPE_ID_HEADER = "__TypeId__";

	private final OutboxMessageRepository outboxMessageRepository;
	private final JacksonJsonSerializer<Object> outboxJsonSerializer;

	@Transactional(propagation = Propagation.MANDATORY)
	public void record(String channelName, String aggregateId, Object message) {
		RecordHeaders headers = new RecordHeaders();
		byte[] payload = outboxJsonSerializer.serialize(channelName, headers, message);

		String eventType = readEventType(headers, message);

		if (payload != null) {
			OutboxMessage outboxMessage = OutboxMessage.builder()
					.channelName(channelName)
					.aggregateId(aggregateId)
					.payload(new String(payload, StandardCharsets.UTF_8))
					.eventType(eventType)
					.build();

			outboxMessageRepository.save(outboxMessage);

            log.info("Recorder {} on outbox: channel={} | aggregateId={} | id={}",
	            message.getClass().getSimpleName(), channelName, aggregateId, outboxMessage.getId());
		}
	}

	private String readEventType(RecordHeaders headers, Object message) {
		Header typeId = headers.lastHeader(TYPE_ID_HEADER);

		if (typeId == null) return message.getClass().getName();

		return new String(typeId.value(), StandardCharsets.UTF_8);
	}
}
