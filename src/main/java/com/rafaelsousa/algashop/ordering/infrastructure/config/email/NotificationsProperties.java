package com.rafaelsousa.algashop.ordering.infrastructure.config.email;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Data
@Component
@Validated
@ConfigurationProperties("algashop.notifications")
public class NotificationsProperties {

	@NotNull @Valid
	private EmailTemplates emailTemplates;

	@Data
	public static class EmailTemplates {

		@NotNull
		private UUID orderPlaced;
	}
}
