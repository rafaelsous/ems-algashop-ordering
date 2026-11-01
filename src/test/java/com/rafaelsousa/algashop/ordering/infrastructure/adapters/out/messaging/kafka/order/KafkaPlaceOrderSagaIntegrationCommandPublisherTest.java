package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.kafka.order;

import com.rafaelsousa.algashop.ordering.core.application.CommandPublishingException;
import com.rafaelsousa.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.application.invoice.command.CancelInvoiceIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.application.invoice.command.IssueInvoiceIntegrationCommand;
import com.rafaelsousa.algashop.ordering.core.application.stock.command.ReserveStockIntegrationCommand;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.saga.PlaceOrderSagaCommandTopics;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import com.rafaelsousa.algashop.ordering.infrastructure.config.kafka.KafkaConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class KafkaPlaceOrderSagaIntegrationCommandPublisherTest {
	private static final String BILLING_CHANNEL_NAME = "billing.invoice.commands";
	private static final String STOCK_CHANNEL_NAME = "product-catalog.stock.commands";
	private static final String REPLIES_CHANNEL_NAME = "ordering.saga.replies";

	private static final String ORDER_ID = "0S3KSMGQZDXWP";
	private static final String CORRELATION_ID = UUID.randomUUID().toString();

	@SuppressWarnings("unchecked")
	private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);

	private KafkaPlaceOrderSagaIntegrationCommandPublisher commandPublisher;

	@BeforeEach
	void setup() {
		AlgaShopMessagingKafkaProperties properties = new AlgaShopMessagingKafkaProperties();
		properties.setSagaRepliesTopicName(REPLIES_CHANNEL_NAME);
		properties.setBillingInvoiceCommandsTopicName(BILLING_CHANNEL_NAME);
		properties.setProductCatalogStockCommandsTopicName(STOCK_CHANNEL_NAME);

		PlaceOrderSagaCommandTopics placeOrderSagaCommandTopics = new PlaceOrderSagaCommandTopics(properties);
		commandPublisher = new KafkaPlaceOrderSagaIntegrationCommandPublisher(
			placeOrderSagaCommandTopics,
			kafkaTemplate,
			properties);
	}

	@Test
	void shouldPublishIssueInvoiceIntegrationCommandForOrder() {
		givenKafkaAcknowledges();

		IssueInvoiceIntegrationCommand command = new IssueInvoiceIntegrationCommand() {
			@Override
			public UUID getIdempotencyKey() {
				return UUID.randomUUID();
			}
		};
		command.setOrderId(ORDER_ID);

		commandPublisher.send(CORRELATION_ID, command);

		ProducerRecord<String, Object> obtainedRecord = obtainRecord();

		assertThat(obtainedRecord).satisfies(record -> {
			assertThat(record.key()).isEqualTo(ORDER_ID);
			assertThat(record.value()).isSameAs(command);
			assertThat(record.topic()).isEqualTo(BILLING_CHANNEL_NAME);
			assertThat(headerOf(record, KafkaConfig.IDEMPOTENCY_KEY_HEADER)).isNotNull();
			assertThat(headerOf(record, KafkaHeaders.CORRELATION_ID)).isEqualTo(CORRELATION_ID);
			assertThat(headerOf(record, KafkaHeaders.REPLY_TOPIC)).isEqualTo(REPLIES_CHANNEL_NAME);
		});
	}

	@Test
	void shouldPublishCancelInvoiceIntegrationCommandForOrder() {
		givenKafkaAcknowledges();

		CancelInvoiceIntegrationCommand command = new CancelInvoiceIntegrationCommand(ORDER_ID);

		commandPublisher.send(CORRELATION_ID, command);

		ProducerRecord<String, Object> record = obtainRecord();

		assertThat(record).satisfies(r -> {
			assertThat(r.key()).isEqualTo(ORDER_ID);
			assertThat(r.value()).isSameAs(command);
			assertThat(command.getIdempotencyKey()).isNull();
			assertThat(r.topic()).isEqualTo(BILLING_CHANNEL_NAME);
			assertThat(headerOf(r, KafkaHeaders.CORRELATION_ID)).isEqualTo(CORRELATION_ID);
			assertThat(headerOf(r, KafkaHeaders.REPLY_TOPIC)).isEqualTo(REPLIES_CHANNEL_NAME);
		});
	}

	@Test
	void shouldPublishReserveStockIntegrationCommandForOrder() {
		givenKafkaAcknowledges();

		ReserveStockIntegrationCommand command = new ReserveStockIntegrationCommand(ORDER_ID, List.of(
			new ReserveStockIntegrationCommand.Item(UUID.randomUUID(), 2),
			new ReserveStockIntegrationCommand.Item(UUID.randomUUID(), 5)
		));

		commandPublisher.send(CORRELATION_ID, command);

		ProducerRecord<String, Object> obtainedRecord = obtainRecord();

		assertThat(obtainedRecord).satisfies(record -> {
			assertThat(record.key()).isEqualTo(ORDER_ID);
			assertThat(record.value()).isSameAs(command);
			assertThat(command.getIdempotencyKey()).isNull();
			assertThat(record.topic()).isEqualTo(STOCK_CHANNEL_NAME);
			assertThat(headerOf(record, KafkaHeaders.CORRELATION_ID)).isEqualTo(CORRELATION_ID);
			assertThat(headerOf(record, KafkaHeaders.REPLY_TOPIC)).isEqualTo(REPLIES_CHANNEL_NAME);
		});
	}

	@Test
	void shouldThrowsCommandPublishingExceptionWhenFailSend() {
		when(kafkaTemplate.send(Mockito.<ProducerRecord<String, Object>>any()))
			.thenReturn(CompletableFuture.failedFuture(new RuntimeException("Failed to publish command")));

		IssueInvoiceIntegrationCommand command = new IssueInvoiceIntegrationCommand();
		command.setOrderId(ORDER_ID);

		assertThatThrownBy(() -> commandPublisher.send(CORRELATION_ID, command))
			.isInstanceOf(CommandPublishingException.class)
			.hasMessageContaining("Failed to publish command");
	}

	@Test
	void shouldThrowsCommandPublishingExceptionWhenWithInterruptionExceptionAsRootCause() throws Exception {
		@SuppressWarnings("unchecked")
		CompletableFuture<SendResult<String, Object>> future = mock(CompletableFuture.class);

		when(kafkaTemplate.send(Mockito.<ProducerRecord<String, Object>>any()))
			.thenReturn(future);

		InterruptedException rootCause = new InterruptedException("Interrupted while publishing");

		when(future.get(40, TimeUnit.SECONDS)).thenThrow(rootCause);

		IssueInvoiceIntegrationCommand command = new IssueInvoiceIntegrationCommand();
		command.setOrderId(ORDER_ID);

		try{
			assertThatThrownBy(() -> commandPublisher.send(CORRELATION_ID, command))
				.hasRootCauseInstanceOf(InterruptedException.class)
				.hasRootCauseMessage("Interrupted while publishing");
		} finally{
			// Limpa o status de interrupção para não afetar outros testes
			boolean interrupted = Thread.interrupted();
			assertTrue(interrupted);
		}
	}

	@Test
	void shouldThrowsIllegalArgumentationExceptionWhenUnknownCommand() {
		OutboundIntegrationCommand unknownCommand = () -> ORDER_ID;

		assertThatIllegalArgumentException()
			.isThrownBy(() -> commandPublisher.send(CORRELATION_ID, unknownCommand))
			.isInstanceOf(IllegalArgumentException.class)
			.withMessageContaining("Unsupported saga command");
		verifyNoInteractions(kafkaTemplate);
	}

	private void givenKafkaAcknowledges() {
		RecordMetadata metadata = new RecordMetadata(new TopicPartition(BILLING_CHANNEL_NAME, 0), 0L, 0, 0L, 0, 0);

		when(kafkaTemplate.send(Mockito.<ProducerRecord<String, Object>>any()))
			.thenAnswer(invocation -> CompletableFuture.completedFuture(
				new SendResult<>(invocation.getArgument(0), metadata)));
	}

	private ProducerRecord<String, Object> obtainRecord() {
		@SuppressWarnings("unchecked")
		ArgumentCaptor<ProducerRecord<String, Object>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
		verify(kafkaTemplate).send(captor.capture());

		return captor.getValue();
	}

	private static @NonNull String headerOf(ProducerRecord<String, Object> record, String headerName) {
		return new String(record.headers().lastHeader(headerName).value(), StandardCharsets.UTF_8);
	}
}
