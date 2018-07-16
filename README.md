# Spring Batch Performance Monitoring (SBPM)
This Tool provides the means to monitor the performance of Spring Batch applications without the need to manipulate the respective code basis.

Through Listeners and AOP it evaluates the throughput of a monitored Job and writes the result to a simple database-schema (a file-based H2 by default).

It measures the performance of Job, Step, Chunk, Reader/Processor/Writer/Tasklet down to indivdual Items.

## Installation/Usage

1. Add the dependency to your POM:
```xml
<dependency>
  <groupId>de.viadee</groupId>
  <artifactId>springBatchPerformanceMonitoring</artifactId>
  <version>...</version>
</dependency>
```
2. Add the de.viadee.spring.batch.infrastructure.Configurator.class to your Spring-Application-Context (via @Import on your Configuration) 
3. Run the Job
4. By default the monitoring result is written to project-folder/target/database/monitoringDB.mv.db

You can access the database with the credentials sa/sasa. It contains all perfomance data measured in snowflake style as well as number of prepared views for typical questions on all the detail levels mentioned above. You can see the [SQL schema in H2 syntax in the code](https://github.com/viadee/springBatchPerformanceMonitoring/blob/master/src/main/resources/SQL/schema-h2.sql).

## Commitments
This library will remain under an open source licence indefinately.

We follow the [semantic versioning](http://semver.org) scheme (2.0.0).

In the sense of semantic versioning, the resulting database schema is the _only public API_ provided here. We will keep the database schema as stable as possible, in order to enable users to analyse performance logs with the toolsets of their choice.

For the same reason, we do not use any database specific features and we minimize the assumptions made regarding Spring versions and the whole runtime environment.

## Cooperation
Feel free to add issues, questions, ideas or patches. We are looking forward to it.

[![Build Status](https://travis-ci.org/viadee/springBatchPerformanceMonitoring.svg?branch=master)](https://travis-ci.org/viadee/springBatchPerformanceMonitoring)

## License (BSD 3-Clause License)

Copyright (c) 2018, viadee IT-Unternehmensberatung AG
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
