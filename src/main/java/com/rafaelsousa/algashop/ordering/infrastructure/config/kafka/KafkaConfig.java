package com.rafaelsousa.algashop.ordering.infrastructure.config.kafka;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import java.time.Duration;
import java.util.Map;

@Configuration
public class KafkaConfig {
    public static final String MIN_INSYNC_REPLICAS_CONFIG = "min.insync.replicas";
    public static final String TYPE_ID_HEADER = "__TypeId__";
    public static final String IDEMPOTENCY_KEY_HEADER = "idempotency-key";

    private static final String DLT_PREFIX = "ordering.dlt.";

    @Bean
    public DefaultErrorHandler defaultErrorHandler(DeadLetterPublishingRecoverer recoverer) {
        long interval = 2000L; // 2s entre as tentativas
        double multiplier = 2;
        long maxRetries = 3L; // máximo de 2 tentativas

        ExponentialBackOff exponentialBackOff = new ExponentialBackOff(interval, multiplier);
        exponentialBackOff.setMaxAttempts(maxRetries);

        DefaultErrorHandler defaultErrorHandler =
                new DefaultErrorHandler(recoverer, exponentialBackOff);
        defaultErrorHandler.addNotRetryableExceptions(
                DomainException.class, IllegalArgumentException.class);

        return defaultErrorHandler;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            KafkaTemplate<String, Object> kafkaTemplate,
            AlgaShopMessagingKafkaProperties properties) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (consumerRecord, _) -> {
                            String dltTopic = DLT_PREFIX + consumerRecord.topic();
                            return new TopicPartition(dltTopic, consumerRecord.partition());
                        });

        recoverer.setLogRecoveryRecord(true);
        recoverer.setFailIfSendResultIsError(false);

        return recoverer;
    }

    @Bean
    public NewTopic productEventsDlt(AlgaShopMessagingKafkaProperties properties) {
        return createDeadLetterTopic(properties.getProductEventTopicName());
    }

    @Bean
    public NewTopic orderEventsDlt(AlgaShopMessagingKafkaProperties properties) {
        return createDeadLetterTopic(properties.getOrderEventTopicName());
    }

    @Bean
    public NewTopic ordersEventsTopic(AlgaShopMessagingKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrderEventTopicName())
                .partitions(3)
                .replicas(3)
                .configs(Map.of(MIN_INSYNC_REPLICAS_CONFIG, "2"))
                .build();
    }

    @Bean
    public NewTopic ordersCommandsTopic(AlgaShopMessagingKafkaProperties properties) {
        return TopicBuilder.name(properties.getOrderCommandTopicName())
                .partitions(3)
                .replicas(3)
                .configs(Map.of(MIN_INSYNC_REPLICAS_CONFIG, "2"))
                .build();
    }

    private NewTopic createDeadLetterTopic(String originTopicName) {
        return TopicBuilder.name(DLT_PREFIX + originTopicName)
                .partitions(3)
                .replicas(3)
                .configs(
                        Map.of(
                                MIN_INSYNC_REPLICAS_CONFIG,
                                "2",
                                "retention.ms",
                                String.valueOf(Duration.ofDays(30).toMillis())))
                .build();
    }
}
