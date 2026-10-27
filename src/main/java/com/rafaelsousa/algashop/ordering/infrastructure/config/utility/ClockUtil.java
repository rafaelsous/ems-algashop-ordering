package com.rafaelsousa.algashop.ordering.infrastructure.config.utility;

import java.time.Clock;

public class ClockUtil {

	public static Clock getDefaultTimeZone() {
		return Clock.systemDefaultZone();
	}
}
