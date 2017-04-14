# Spring Batch Performance Monitoring (SBPM)
This Tool provides the means to monitor the performance of Spring Batch applications without the need to manipulate the respective code basis.

Through Listeners and AOP it evaluates the throughput of a monitored Job and writes the result to a simple database-schema (a file-based H2 by default).

It measures the performance of Job, Step, Chunk, Reader/Processor/Writer/Tasklet down to indivdual Items.

## Installation/Usage

1. Add the sbpm-jar to your classpath (maven-repository to follow, until then you also have to add [H2](https://mvnrepository.com/artifact/com.h2database/h2/1.4.194) and [AspectJ Weaver](https://mvnrepository.com/artifact/org.aspectj/aspectjweaver/1.8.6) to your POM).
2. Add the de.viadee.spring.batch.infrastructure.Configurator.class to your Spring-Application-Context (via @Import on your Configuration) 
3. Run the Job
4. By default the monitoring result is written to project-folder/target/database/monitoringDB.mv.db

You can access the database with the credentials sa/sasa. It contains a perfomance data in snowflake style as well as number of prepared views for typical questions on all the detail levels mentioned above. 

## Commitments
This library will remain under an open source licence indefinately.

We will keep the database schema as stable as possible, in order to enable users to analyse performance logs with the toolsets of their choice.

## Cooperation
Feel free to add issues, questions, ideas or patches. We are looking forward to it.

[![Build Status](https://travis-ci.org/viadee/springBatchPerformanceMonitoring.svg?branch=master)](https://travis-ci.org/viadee/springBatchPerformanceMonitoring)

## License (BSD4)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
    must display the following acknowledgement:
    This product includes software developed by the viadee Unternehmensberatung GmbH.
 4. Neither the name of the viadee Unternehmensberatung GmbH nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
