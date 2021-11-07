package com.github.eventlogprocessor.logmanager;

import com.github.eventlogprocessor.service.impl.ProducerServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LogAnalyserComponentTest {

    @Mock
    private ProducerServiceImpl producerServiceImpl;
    @InjectMocks
    private LogAnalyserComponent logAnalyserComponent;

    @Test
    public void testValidLogFileWithEvents(){

        doNothing().when(producerServiceImpl).produce(anyString(), anyMap(), anyMap(), any(AtomicInteger.class));
        logAnalyserComponent.analyseLogs("logFile_6_Events.txt");
        verify(producerServiceImpl, times(6)).produce(anyString(), anyMap(), anyMap(), any(AtomicInteger.class));
    }
}
