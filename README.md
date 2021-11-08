# Log Event Processor

A simple utility to read, parse and flag the evens from a log file.

## To run the app

1. Clone the git repo
```shell
git clone https://github.com/sujit.bhosale05/event-log-processor.git
```
2. Open the project in an IDE of your choice.
3. Place log file you want to test under path "event-log-processor\src\main\resources\samples" - few Sample files placed under this folder.
4. Run the main Spring Boot app - 'com\github\eventlogprocessor\EventLogProcessorApplication.java`.
    Before running provide programme argument - run -> Edit Configuration -> provide file path (just provide fileName placed under samples folder.)
    eg. - to run 'logFile_200K_Events.txt' under samples folder, provide programme argument as logFile_200K_Events.txt
 
5. While running app you will see status of batch persited and total records persisted - logged with info as below.
		eg. - "Persisted a Batch of 20003 and Total Persisted Alerts 20003" - keeps on updating
6. File DB folder is event-log-processor\db - once apllication processed log file completely you can connect to HSQLDB file DB(File DB url connection provided in application yml).	And check persited records.

==========================================================================================================================================================

Below are a few highlights.
Application is capable of handling huge file of gigabytes. Used Multiple Threads Parallel stream operation and Spring Batch of 20K in one go persist to improve performance. Tested with a gigabyte file, worked as expected without concurrency issues.

To get Performance improvement following things done.
1. Used Parallel stream to iterate over each event - so parallel stream internally allowed multiple threads to perform tasks using Fork Join Pool.
2. Created produce method which put all events in Map check with respective Started/Finished event and create Alert object and put in another Map. In the Produce method, multiple threads of Parallel stream perform operation.
3. Spring Batch is used to persist records in a batch of 20K in the consume method. Batch size provided in application yml. Persisting records in a batch of 20k improved performance.

Following things done to avoid concurrency issues.
1. Consume saveAll batch method is in synchronized block, so only 1 thread at a time save a batch 0f 20k. Also double checking is used inside the Synchronised block to avoid multiple threads getting entered in the Synchronized block.
2. EventMap and AlertsMap are both created as ConcurrentHashMap which don't block for read and block only for write operation. Which is high performance and thread safe.
3. To keep track of total Persisted Records created AtomicInteger variable to make thread safe and atomic operation on persisted count. 