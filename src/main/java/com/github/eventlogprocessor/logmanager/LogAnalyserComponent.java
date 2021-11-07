package com.github.eventlogprocessor.logmanager;

import com.github.eventlogprocessor.model.persistence.Alert;
import com.github.eventlogprocessor.repository.AlertRepository;
import com.github.eventlogprocessor.service.ProducerService;
import com.github.eventlogprocessor.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogAnalyserComponent {

    private final ProducerService producerService;
    private final AlertRepository alertRepository;

    public void analyseLogs(String logPath) {

        Map<String, Event> eventMap= new ConcurrentHashMap<>();
        Map<String, Alert> alertsMap = new ConcurrentHashMap<>();
        AtomicInteger totalPersisted = new AtomicInteger(0);

        try{
            Path path = Paths.get(new ClassPathResource("samples/" + logPath).getURI());
            Files.lines(path).parallel().forEach(line-> producerService.produce(line, eventMap, alertsMap, totalPersisted));

            if (alertsMap.size() != 0) {
                alertRepository.saveAll(alertsMap.values());
                log.info("Persisted a final Batch of {} and Total Persisted Alerts {}",
                        alertsMap.size(), totalPersisted.addAndGet(alertsMap.size()));
            }
        } catch (IOException exception) {
            log.error("{} is not a valid file path::: ", logPath, exception);
        }
    }
}
