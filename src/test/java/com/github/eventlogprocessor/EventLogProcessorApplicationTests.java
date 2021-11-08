package com.github.eventlogprocessor;

import com.github.eventlogprocessor.logmanager.LogAnalyserComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;

@SpringBootTest(args={"logFile_6_Events.txt"})
class EventLogProcessorApplicationTests {

	@Mock
	LogAnalyserComponent logAnalyserComponent;

	@Test
	public void testWithArgs() {
		doNothing().when(logAnalyserComponent).analyseLogs(anyString());
		EventLogProcessorApplication.main(new String[] {"logFile_6_Events.txt"});
	}

	@Test
	public void testWithoutArgs() {

		doNothing().when(logAnalyserComponent).analyseLogs(anyString());
		Exception exception = Assertions.assertThrows(IllegalStateException.class, () ->
				EventLogProcessorApplication.main(new String[] {}));
		String expectedMessage = "Failed to execute CommandLineRunner";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));
	}
}
