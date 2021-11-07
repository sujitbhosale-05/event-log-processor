package com.github.eventlogprocessor.service.impl;

import com.github.eventlogprocessor.model.persistence.Alert;
import com.github.eventlogprocessor.repository.AlertRepository;
import com.github.eventlogprocessor.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final AlertRepository alertRepository;
    private final int alertsBatchPersistSize;

    public ConsumerServiceImpl(AlertRepository alertRepository,
                               @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
                                       int alertsBatchPersistSize) {
        this.alertRepository = alertRepository;
        this.alertsBatchPersistSize = alertsBatchPersistSize;
    }

    @Override
    public void consume(Map<String, Alert> alertsMap, AtomicInteger totalPersistedRecords){
        synchronized(this) {
            if (alertsMap.size() >= alertsBatchPersistSize) {
                log.debug("Inside Consume :: " +Thread.currentThread().getName());
                log.trace("Persisting {} alerts...", alertsMap.keySet());
                alertRepository.saveAll(alertsMap.values());
                log.info("Persisted a Batch of {} and Total Persisted Alerts {}",
                        alertsMap.size(), totalPersistedRecords.addAndGet(alertsMap.size()));
                alertsMap.clear();
            }
        }
    }
}
