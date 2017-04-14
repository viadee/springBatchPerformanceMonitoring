# Spring Batch Performance Monitoring (SBPM)
This Tool provides the means to monitor the performance of Spring Batch applications without the need to manipulate the respective code basis.

Through Listeners and AOP it evaluates the throughput of a monitored Job and writes the result to a simble database-schema (a file-based H2 by default).

It measures the performance of Job, Step, Chunk, Reader/Processor/Writer/Tasklet down to indivdual Items.

## Installation/Usage

1. Add the sbpm-jar to your classpath (maven-repository to follow, until then you also have to add [H2](https://mvnrepository.com/artifact/com.h2database/h2/1.4.194) and [AspectJ Weaver](https://mvnrepository.com/artifact/org.aspectj/aspectjweaver/1.8.6) to your POM).
2. Add the de.viadee.spring.batch.infrastructure.Configurator.class to your Spring-Application-Context (via @Import on your Configuration) 
3. Run the Job
4. By default the monitoring result is written to project-folder/target/database/monitoringDB.mv.db
