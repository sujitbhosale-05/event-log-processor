package com.github.eventlogprocessor.repository;

import com.github.eventlogprocessor.model.persistence.Alert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(args={"logFile_6_Events.txt"})
public class AlertRepositoryTest {

    @Autowired
    private AlertRepository repository;

    @Test
    public void whenTwoAlertsInBatchPersisted() {

        Alert alert1 = new Alert("alert-1", 3, false, "APPLICATION_LOG", "121.1.1.4");
        Alert alert2 = new Alert("alert-2", 9, true, null, null);
        List<Alert> alertList = Arrays.asList(alert1, alert2);
        repository.saveAll(alertList);
        assertNotNull(repository.findAll());

        Alert result1 = repository.findById("alert-1").isPresent()?repository.findById("alert-1").get():null;
        assertNotNull(result1);
        assertEquals("alert-1", result1.getEventId());
        assertEquals(3, result1.getEventDuration());
        assertFalse(result1.getIsAlert());
        assertEquals("121.1.1.4", result1.getHost());
        assertEquals("APPLICATION_LOG", result1.getEventType());

        Alert result2 = repository.findById("alert-2").isPresent()?repository.findById("alert-2").get():null;
        assertNotNull(result2);
        assertEquals("alert-2", result2.getEventId());
        assertEquals(9, result2.getEventDuration());
        assertTrue(result2.getIsAlert());
        assertNull(result2.getHost());
        assertNull(result2.getEventType());
    }
}