package com.github.eventlogprocessor.service;

import com.github.eventlogprocessor.model.persistence.Alert;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface ConsumerService {

    void consume(Map<String, Alert> alerts, AtomicInteger totalPersisted);
}
