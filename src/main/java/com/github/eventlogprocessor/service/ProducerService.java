package com.github.eventlogprocessor.service;

import com.github.eventlogprocessor.model.persistence.Alert;
import com.github.eventlogprocessor.model.Event;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public interface ProducerService {

    void produce(String line, Map<String, Event> map, Map<String, Alert> alerts, AtomicInteger totalPersisted);
}
