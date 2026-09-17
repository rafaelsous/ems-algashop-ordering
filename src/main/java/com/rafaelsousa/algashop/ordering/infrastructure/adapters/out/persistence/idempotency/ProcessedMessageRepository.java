package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, UUID> {

}
