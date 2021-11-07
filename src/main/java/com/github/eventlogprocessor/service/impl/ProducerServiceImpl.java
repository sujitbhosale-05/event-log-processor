package com.github.eventlogprocessor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eventlogprocessor.service.ConsumerService;
import com.github.eventlogprocessor.model.Event;
import com.github.eventlogprocessor.model.State;
import com.github.eventlogprocessor.model.persistence.Alert;
import com.github.eventlogprocessor.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ProducerServiceImpl implements ProducerService {

    private final ConsumerService consumerService;
    private final int alertThresholdMs;
    private final int alertsBatchPersistSize;

    @Autowired
    public ProducerServiceImpl(ConsumerService consumerService,
                               @Value("${app.event-log-processor.alert-threshold-ms}")
                                       int alertThresholdMs,
                               @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
                                       int alertsBatchPersistSize) {
        this.consumerService = consumerService;
        this.alertThresholdMs = alertThresholdMs;
        this.alertsBatchPersistSize = alertsBatchPersistSize;
    }

    @Override
    public void produce(String line, Map<String, Event> eventMap, Map<String, Alert> alertsMap, AtomicInteger totalPersistedRecords){
        try {
            log.trace("Inside Produce :: " +Thread.currentThread().getName());
            Event eventFromLogs = new ObjectMapper().readValue(line, Event.class);

            if(eventMap.containsKey(eventFromLogs.getId())){
                Event eventFromMap=eventMap.get(eventFromLogs.getId());
                long eventDuration = getEventDuration(eventFromMap, eventFromLogs);
                boolean alertFlag = eventDuration>alertThresholdMs;
                Alert alert = new Alert(eventFromLogs.getId(), eventDuration, alertFlag,
                        eventFromLogs.getType(), eventFromLogs.getHost());
                alertsMap.put(eventFromLogs.getId(), alert);
                eventMap.remove(eventFromLogs.getId());
            }else{
                eventMap.put(eventFromLogs.getId(), eventFromLogs);
            }

            if (alertsMap.size() >= alertsBatchPersistSize) {
                consumerService.consume(alertsMap, totalPersistedRecords);
            }
        }catch(Exception exception){
            log.error("Error while Processing Events ::: " +exception);
            exception.printStackTrace();
        }
    }

    public long getEventDuration(Event eventFromMap, Event eventFromLog){
        long startedTime = State.STARTED.name().equals(eventFromMap.getState().name())?eventFromMap.getTimestamp():eventFromLog.getTimestamp();
        long endTime = State.FINISHED.name().equals(eventFromMap.getState().name())?eventFromMap.getTimestamp():eventFromLog.getTimestamp();
        return (endTime - startedTime);
    }
}
