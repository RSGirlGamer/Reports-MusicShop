package com.project.reportsmusicshop.test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

import com.amazonaws.auth.internal.AWS4SignerUtils;

class AWSTest {

	@Test
	void getLocalDateTime() {
		Instant instant = Instant.now();
		Long milliSeconds = instant.toEpochMilli();
		System.out.println("1615503903360");
		LocalDateTime localDateTime = LocalDateTime.now();
		String time = localDateTime.format(DateTimeFormatter.ofPattern("YYYYMMdd" + "'T'" + "hhmmss" + "'Z'"));
		System.out.println(time);
		String date = AWS4SignerUtils.formatTimestamp(milliSeconds);
		System.out.println(date);
		
	}
	@Test
	void parameterAWS() {
		
	}

}
