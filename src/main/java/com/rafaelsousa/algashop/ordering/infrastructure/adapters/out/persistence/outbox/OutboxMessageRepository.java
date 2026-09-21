package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.outbox;

import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.messaging.outbox.OutboxMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {

	@Query("SELECT om FROM OutboxMessage om ORDER BY om.id")
	List<OutboxMessage> findBatch(Pageable pageable);
}
