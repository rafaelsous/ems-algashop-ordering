package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.outbox;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox.OutboxMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {

    @Query("SELECT om FROM OutboxMessage om ORDER BY om.id")
    List<OutboxMessage> findBatch(Pageable pageable);

    @Modifying
    @Query("DELETE from OutboxMessage om WHERE om.id = :messageId")
    void deleteMessage(UUID messageId);

    @Modifying
    @Query("""
		UPDATE OutboxMessage om
			set om.attempts = :attempts,
				om.nextAttemptAt = :nextAttemptAt,
				om.lastError = :lastError,
				om.failedAt = :failedAt
		WHERE om.id = :messageId
	""")
    void registerFailed(
            @Param("messageId") UUID messageId,
            @Param("attempts") int attempts,
            @Param("nextAttemptAt") OffsetDateTime nextAttemptAt,
            @Param("lastError") String lastError,
            @Param("failedAt") OffsetDateTime failedAt);
}
