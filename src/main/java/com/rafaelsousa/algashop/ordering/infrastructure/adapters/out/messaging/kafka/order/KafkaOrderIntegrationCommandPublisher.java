package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.kafka.order;

import com.rafaelsousa.algashop.ordering.core.application.CommandPublishingException;
import com.rafaelsousa.algashop.ordering.core.application.IntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationCommands;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import com.rafaelsousa.algashop.ordering.infrastructure.config.utility.BeanValidationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderIntegrationCommandPublisher implements ForPublishingOrderIntegrationCommands {
	private final BeanValidationUtil beanValidationUtil;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final AlgaShopMessagingKafkaProperties properties;

	@Override
	public void send(IntegrationCommand command) {
		beanValidationUtil.validate(command);

		SendResult<String, Object> result;
		try {
			ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(properties.getOrderCommandTopicName(), command.getAggregateId(), command);

			result = kafkaTemplate.send(producerRecord).get(40, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
            throw new CommandPublishingException("Interrupted while publishing", command, ex);
		} catch (TimeoutException | ExecutionException ex) {
			throw new CommandPublishingException("Failed to publish command", command, ex);
		}

		RecordMetadata metadata = result.getRecordMetadata();

		log.info(
			"Published {} to {}-{} at offset {}",
			command.getClass().getSimpleName(),
			metadata.topic(),
			metadata.partition(),
			metadata.offset());
	}
}
