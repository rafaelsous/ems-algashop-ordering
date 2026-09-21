package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.idempotency;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "processed_message")
@NoArgsConstructor
public class ProcessedMessage implements Persistable<UUID> {

	@Id
	private UUID idempotencyKey;

	public ProcessedMessage(UUID idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	@Override
	public @Nullable UUID getId() {
		return idempotencyKey;
	}

	@Override
	public boolean isNew() {
		return true; // Essa tabela só insere, nunca atualiza
	}
}
