package com.github.eventlogprocessor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.eventlogprocessor.model.Event;
import com.github.eventlogprocessor.model.persistence.Alert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProducerServiceImplTest {

    @Mock
    private ConsumerServiceImpl consumerServiceImpl;
    private ProducerServiceImpl producerServiceImpl;
    private Map<String, Event> eventMap;
    private Map<String, Alert> alertsMap;
    private AtomicInteger totalPersisted;
    private static final int alertThreshold = 4;
    private static final int alertBatchPersistSize = 3;
    private static final String eventAStartedJson = "{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", " +
            "\"host\":\"12345\", \"timestamp\":1491377495212}";
    private static final String eventAFinishedJson = "{\"id\":\"scsmbstgra\", \"state\":\"FINISHED\", \"type\":\"APPLICATION_LOG\", " +
            "\"host\":\"12345\", \"timestamp\":1491377495217}";
    private static final String eventBStartedJson = "{\"id\":\"scsmbstgrb\", \"state\":\"STARTED\", \"timestamp\":1491377495213}";
    private static final String eventBFinishedJson = "{\"id\":\"scsmbstgrb\", \"state\":\"FINISHED\", \"timestamp\":1491377495216}";
    private static final String eventCStartedJson = "{\"id\":\"scsmbstgrc\", \"state\":\"STARTED\", \"timestamp\":1491377495210}";
    private static final String eventCFinishedJson = "{\"id\":\"scsmbstgrc\", \"state\":\"FINISHED\", \"timestamp\":1491377495218}";
    private static final String eventAID = "scsmbstgra";
    private static final String eventBID = "scsmbstgrb";
    private static final String eventCID = "scsmbstgrc";

    @Before
    public void setUp() {
        producerServiceImpl = new ProducerServiceImpl(consumerServiceImpl, alertThreshold, alertBatchPersistSize);
        eventMap = new ConcurrentHashMap<>();
        alertsMap = new ConcurrentHashMap<>();
        totalPersisted = new AtomicInteger(0);
    }

    @Test
    public void testStartedEventExistInEventMap() throws JsonProcessingException {

        Event eventAStarted = new ObjectMapper().readValue(eventAStartedJson, Event.class);
        eventMap.put(eventAID, eventAStarted);

        producerServiceImpl.produce(eventAFinishedJson, eventMap, alertsMap, totalPersisted);
        assertTrue(eventMap.isEmpty());
        assertFalse(alertsMap.isEmpty());
        assertNotNull(alertsMap.get(eventAID));

        Alert alert = alertsMap.get(eventAID);
        assertEquals(eventAID, alert.getEventId());
        assertEquals(5, alert.getEventDuration());
        assertTrue(alert.getIsAlert());
        assertEquals("APPLICATION_LOG", alert.getEventType());
        assertEquals("12345", alert.getHost());
    }

    @Test
    public void testFinishedEventExistInEventMap() throws JsonProcessingException {

        Event eventBFinished = new ObjectMapper().readValue(eventBFinishedJson, Event.class);
        eventMap.put(eventBID, eventBFinished);

        producerServiceImpl.produce(eventBStartedJson, eventMap, alertsMap, totalPersisted);
        assertTrue(eventMap.isEmpty());
        assertFalse(alertsMap.isEmpty());
        assertNotNull(alertsMap.get(eventBID));

        Alert alert = alertsMap.get(eventBID);
        assertEquals(eventBID, alert.getEventId());
        assertEquals(3, alert.getEventDuration());
        assertFalse(alert.getIsAlert());
        assertNull(alert.getEventType());
        assertNull(alert.getHost());
    }

    @Test
    public void testNoIdenticalEventExistInEventMap() throws JsonProcessingException {

        Event eventAStarted = new ObjectMapper().readValue(eventAStartedJson, Event.class);
        eventMap.put(eventAID, eventAStarted);

        producerServiceImpl.produce(eventCFinishedJson, eventMap, alertsMap, totalPersisted);
        assertFalse(eventMap.isEmpty());
        assertEquals(2, eventMap.size());
        assertTrue(alertsMap.isEmpty());
        assertNotNull(eventMap.get(eventAID));
        assertNotNull(eventMap.get(eventCID));
    }

    @Test
    public void testConsumeOnReachingBatchPersistSize() throws JsonProcessingException{

        Event eventAStarted = new ObjectMapper().readValue(eventAStartedJson, Event.class);
        Event eventBFinished = new ObjectMapper().readValue(eventBFinishedJson, Event.class);
        Event eventCStarted = new ObjectMapper().readValue(eventCStartedJson, Event.class);
        eventMap.put(eventAID, eventAStarted);
        eventMap.put(eventBID, eventBFinished);
        eventMap.put(eventCID, eventCStarted);

        doNothing().when(consumerServiceImpl).consume(alertsMap, totalPersisted);
        producerServiceImpl.produce(eventAFinishedJson, eventMap, alertsMap, totalPersisted);
        producerServiceImpl.produce(eventBStartedJson, eventMap, alertsMap, totalPersisted);
        producerServiceImpl.produce(eventCFinishedJson, eventMap, alertsMap, totalPersisted);

        verify(consumerServiceImpl, times(1)).consume(alertsMap, totalPersisted);
        assertTrue(eventMap.isEmpty());
        assertFalse(alertsMap.isEmpty());
        assertEquals(3, alertsMap.size());
        assertNotNull(alertsMap.get(eventAID));
        assertNotNull(alertsMap.get(eventBID));
        assertNotNull(alertsMap.get(eventCID));

    }

    @Test
    public void testForExceptionBlock() throws JsonProcessingException{

        Event eventAStarted = new ObjectMapper().readValue(eventAStartedJson, Event.class);
        Event eventBFinished = new ObjectMapper().readValue(eventBFinishedJson, Event.class);
        Event eventCStarted = new ObjectMapper().readValue(eventCStartedJson, Event.class);
        eventMap.put(eventAID, eventAStarted);
        eventMap.put(eventBID, eventBFinished);
        eventMap.put(eventCID, eventCStarted);

        doThrow(new RuntimeException()).when(consumerServiceImpl).consume(alertsMap, totalPersisted);
        producerServiceImpl.produce(eventAFinishedJson, eventMap, alertsMap, totalPersisted);
        producerServiceImpl.produce(eventBStartedJson, eventMap, alertsMap, totalPersisted);
        producerServiceImpl.produce(eventCFinishedJson, eventMap, alertsMap, totalPersisted);

        verify(consumerServiceImpl, times(1)).consume(alertsMap, totalPersisted);
    }
}
