package com.github.eventlogprocessor;

import com.github.eventlogprocessor.logmanager.LogAnalyserComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class EventLogProcessorApplication implements CommandLineRunner {

	private final LogAnalyserComponent logAnalyserComponent;

	public static void main(String[] args) {
		SpringApplication.run(EventLogProcessorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("Executing Command Line Runner :::::");
		Instant start = Instant.now();
		if(args.length<1){
			throw new IllegalArgumentException("Please provide Log File Directory Path in Programme Argument");
		}
		log.info("Log file specified for EventLogProcessorApplication: {}", args[0]);
		logAnalyserComponent.analyseLogs(args[0]);
		Instant end = Instant.now();
		log.info("Done!!! Finished Log Event Processing");
		log.info("Total time taken to execute Operation ::: {} ms", Duration.between(start, end).toMillis());
	}
}