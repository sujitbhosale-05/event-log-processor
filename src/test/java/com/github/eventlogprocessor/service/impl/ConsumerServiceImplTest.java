package com.github.eventlogprocessor.service.impl;

import com.github.eventlogprocessor.model.persistence.Alert;
import com.github.eventlogprocessor.repository.AlertRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerServiceImplTest {

    @Mock
    private AlertRepository alertRepository;
    private ConsumerServiceImpl consumerServiceImpl;
    private Map<String, Alert> alertsMap;
    private static final int alertBatchPersistSize = 2;
    private AtomicInteger totalPersisted;

    @Before
    public void setUp() {
        consumerServiceImpl = new ConsumerServiceImpl(alertRepository, alertBatchPersistSize);
        alertsMap = new ConcurrentHashMap<>();
        totalPersisted = new AtomicInteger(0);
    }

    @Test
    public void testSaveBatchWhenAlertBatchSizeAsExpectedSize() {

        Alert alert1 = new Alert("alert-1", 10, true, "APPLICATION_LOG", "121.1.1.4");
        Alert alert2 = new Alert("alert-2", 3, false, "APPLICATION_LOG", "131.1.1.4");
        Alert alert3 = new Alert("alert-3", 4, false, null, null);

        alertsMap.put("alert-1", alert1);
        alertsMap.put("alert-2", alert2);
        alertsMap.put("alert-3", alert3);

        when(alertRepository.saveAll(alertsMap.values())).thenReturn(Arrays.asList(alert1, alert2, alert3));
        consumerServiceImpl.consume(alertsMap, totalPersisted);
        verify(alertRepository, times(1)).saveAll(alertsMap.values());
        assertTrue(alertsMap.isEmpty());
        assertEquals(3, totalPersisted.get());
    }

    @Test
    public void testNoSaveBatchWhenAlertBatchSizeNotAsExpectedSize() {

        Alert alert1 = new Alert("alert-1", 10, true, "APPLICATION_LOG", "121.1.1.4");
        alertsMap.put("alert-1", alert1);
        consumerServiceImpl.consume(alertsMap, new AtomicInteger(0));
        verify(alertRepository, times(0)).saveAll(alertsMap.values());
        assertFalse(alertsMap.isEmpty());
        assertEquals(1, alertsMap.size());
        assertEquals(0, totalPersisted.get());
    }
}
