package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.saga;

import com.rafaelsousa.algashop.ordering.core.application.saga.SagaStatus;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@ToString(of = {"sagaId", "status"})
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "saga_instance")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "saga_type", length = 50)
public abstract class SagaInstancePersistence {

	@Id
	@EqualsAndHashCode.Include
	private UUID sagaId;

	private String aggregateId;

	@Enumerated(EnumType.STRING)
	private SagaStatus status;

	@CreatedDate
	private OffsetDateTime createdAt;

	@LastModifiedDate
	private OffsetDateTime updatedAt;

	@Version
	private long version;
}
