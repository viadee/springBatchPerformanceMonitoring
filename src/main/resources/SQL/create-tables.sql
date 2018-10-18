-- Disable referential integrity checks for altering the DB
SET REFERENTIAL_INTEGRITY FALSE;

-- Disable Log to get a bit more Speedup
SET LOG 0;

-- Disable UndoLog to get a bit more Speedup
SET UNDO_LOG 0;

CREATE TABLE IF NOT EXISTS "BatchRuns" (
  "ProcessID" int(11) NOT NULL AUTO_INCREMENT,
  "JobID" int(11) NOT NULL,
  "StepID" int(11) NOT NULL,
  "ActionType" int(11) NOT NULL,
  "JobName" varchar(255) NOT NULL,
  "StepName" varchar(255) NOT NULL,
  "StepStart" BIGINT NOT NULL,
  "StepEnd" BIGINT NOT NULL,
  "ActionName" varchar(255) NOT NULL,
  "TotalTime" int(11) NOT NULL,
  "ProcessedItems" int(11) NOT NULL,
  "MeanTimePerItem" int(11) NOT NULL,
  PRIMARY KEY ("ProcessID")
);

-- Clean the whole DB (Except BatchRuns)
-- DROP ALL OBJECTS;
DROP VIEW "ActionTotalTime" IF EXISTS;
DROP VIEW "ChunkExecutionByTime" IF EXISTS;
DROP VIEW "Denormalized" IF EXISTS;
DROP VIEW "ItemDuration" IF EXISTS;
DROP VIEW "LongestActionPerChunkExecution" IF EXISTS;
DROP VIEW "MeanItemTimeByProcessor" IF EXISTS;
DROP VIEW "Overview" IF EXISTS;

DROP TABLE "LOGS", "TestTable", "Job", "Step", "ChunkExecution", "Action", "StepAction", "Item", "LOGGING_EVENT", "LOGGING_EVENT_PROPERTY", "LOGGING_EVENT_EXCEPTION" IF EXISTS;

-- Create Table for the plain LOG
CREATE TABLE "LOGS"
("USER_ID" VARCHAR(20)    NOT NULL,
"DATED"   TIMESTAMP           NOT NULL,
"LOGGER"  TEXT    NOT NULL,
"LEVEL"   VARCHAR(100)    NOT NULL,
"MESSAGE" TEXT  NOT NULL
);

CREATE TABLE "TestTable"
("TestField" INTEGER    NOT NULL,
);

-- Create Tables for Logback DbAppender
CREATE TABLE "LOGGING_EVENT" (
  timestmp BIGINT NOT NULL,
  formatted_message LONGVARCHAR NOT NULL,
  logger_name VARCHAR(256) NOT NULL,
  level_string VARCHAR(256) NOT NULL,
  thread_name VARCHAR(256),
  reference_flag SMALLINT,
  arg0 VARCHAR(256),
  arg1 VARCHAR(256),
  arg2 VARCHAR(256),
  arg3 VARCHAR(256),
  caller_filename VARCHAR(256), 
  caller_class VARCHAR(256), 
  caller_method VARCHAR(256), 
  caller_line CHAR(4),
  event_id IDENTITY NOT NULL);


CREATE TABLE "LOGGING_EVENT_PROPERTY" (
  event_id BIGINT NOT NULL,
  mapped_key  VARCHAR(254) NOT NULL,
  mapped_value LONGVARCHAR,
  PRIMARY KEY(event_id, mapped_key),
  FOREIGN KEY (event_id) REFERENCES "LOGGING_EVENT"(event_id));

CREATE TABLE "LOGGING_EVENT_EXCEPTION" (
  event_id BIGINT NOT NULL,
  i SMALLINT NOT NULL,
  trace_line VARCHAR(256) NOT NULL,
  PRIMARY KEY(event_id, i),
  FOREIGN KEY (event_id) REFERENCES "LOGGING_EVENT"(event_id));
  
-- Create Tables for persisting the logged Information
CREATE TABLE "Job" (
  "JobID" int(11) NOT NULL,
  "JobName" varchar(255) NOT NULL,
  "JobStart" BIGINT NOT NULL,
  "JobEnd" BIGINT NOT NULL,
  "Duration" int(11) NOT NULL,
  "JobID2" int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY ("JobID", "JobID2")
);

CREATE TABLE "Step" (
  "StepID" int(11) NOT NULL AUTO_INCREMENT,
  "JobID" int(11) NOT NULL,
  "StepName" varchar(255) NOT NULL,
  "StepStart" BIGINT NOT NULL,
  "StepEnd" BIGINT NOT NULL,
  "StepTime" int(11) NOT NULL,
  "StepID2" int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY ("StepID", "StepName", "StepID2")
);

CREATE TABLE "ChunkExecution" (
  "ChunkExecutionID" int(11) NOT NULL AUTO_INCREMENT,
  "StepID" int(11) NOT NULL,
  "StepName" varchar(255) NOT NULL,
  "Iteration" int(11) NOT NULL,
  "ChunkTime" int(11) DEFAULT NULL,
  "ChunkExecutionID2" int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY ("ChunkExecutionID", "ChunkExecutionID2")
);

CREATE TABLE "Action" (
  "ActionID" int(11) NOT NULL,
  "ActionName" varchar(255) NOT NULL,
  "ActionType" int(11) NOT NULL,
  "ActionFather" int(11) DEFAULT NULL,
  "ActionTime" int(11) NOT NULL
);

CREATE TABLE "StepAction" (
  "StepID" int(11) NOT NULL,
  "ActionID" int(11) NOT NULL
);

CREATE TABLE "Item" (
  "ItemID" int(11) NOT NULL AUTO_INCREMENT,
  "ActionID" int(11) NOT NULL,
  "ChunkExecutionID" int(11) NOT NULL,
  "TimeInMS" int(11) DEFAULT NULL,
  "Timestamp" BIGINT NOT NULL,
  "ItemName" varchar(300) NOT NULL,
  "ItemReflection" varchar(1000),
  "ItemClassName" varchar(255),
  "Error" tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY ("ItemID")
);


-- Create the views

-- Denormalized table for copying Data to another Tool
CREATE VIEW "Denormalized" AS (
SELECT "Job"."JobName", "Step"."StepName", "Step"."StepTime", "ChunkExecution"."Iteration", "Action"."ActionName", "Action"."ActionType", "Item"."ItemName", "Item"."ItemClassName", "Item"."Timestamp", "Item"."TimeInMS" FROM "Item"
JOIN "Action" ON "Action"."ActionID" = "Item"."ActionID"
JOIN "ChunkExecution" ON "ChunkExecution"."ChunkExecutionID" = "Item"."ChunkExecutionID"
JOIN "Step" ON "Step"."StepID" = "ChunkExecution"."StepID"
JOIN "Job" ON "Job"."JobID" = "Step"."JobID");

-- Show sum of all item-activity-time per action
CREATE VIEW "ActionTotalTime" AS
SELECT "Action"."ActionName", SUM("Item"."TimeInMS") AS "TotalTime"
FROM "Item" 
JOIN "Action" ON "Action"."ActionID" = "Item"."ActionID"
GROUP BY "Item"."ActionID"
ORDER BY "TotalTime" DESC;

-- Show execution time per chunk execution
CREATE VIEW "ChunkExecutionByTime" AS
SELECT "Step"."StepName", "ChunkExecution"."Iteration", "ChunkExecution"."ChunkTime"
FROM "ChunkExecution"
JOIN "Step" ON "Step"."StepID" = "ChunkExecution"."StepID"
ORDER BY "ChunkExecution"."ChunkTime" DESC;

-- Show duration by item
CREATE VIEW "ItemDuration" AS
SELECT "Item"."ItemID", "Item"."ItemName", CONCAT('Step: ',"Step"."StepName",' - in Chunk ',"ChunkExecution"."ChunkExecutionID",' - Action: ',"Action"."ActionName") as "Where?"
, "Item"."TimeInMS"
FROM "Item"
JOIN "Action" ON  "Action"."ActionID" = "Item"."ActionID"
JOIN "ChunkExecution" ON "ChunkExecution"."ChunkExecutionID" = "Item"."ChunkExecutionID"
JOIN "Step" ON "Step"."StepID" = "ChunkExecution"."StepID"
ORDER BY "Item"."TimeInMS" DESC;

-- Show which action took the longest per chunk
CREATE VIEW "LongestActionPerChunkExecution" AS 
SELECT "Step"."StepName", "ChunkExecution"."Iteration", "Action"."ActionName", SUM("Item"."TimeInMS") as SUM
FROM "Item" 
JOIN "Action" ON "Action"."ActionID" = "Item"."ActionID"
JOIN "ChunkExecution" ON "ChunkExecution"."ChunkExecutionID" = "Item"."ChunkExecutionID"
JOIN "Step" ON "ChunkExecution"."StepID" = "Step"."StepID"
GROUP BY "Item"."ChunkExecutionID", "Item"."ActionID"
ORDER BY SUM DESC;

-- Show the average duration per Item for all Actions
CREATE VIEW "MeanItemTimeByProcessor" AS
SELECT "Action"."ActionName", "RT"."TotalTime", ("RT"."TotalTime"/"ICOUNT"."Count") AS "Mittlere Zeit / Item" from (SELECT SUM("Item"."TimeInMS") AS "TotalTime", "Item"."ActionID" FROM "Item" GROUP BY "Item"."ActionID" HAVING "TotalTime" > 0) as "RT"
JOIN "Action" ON "Action"."ActionID" = "RT"."ActionID"
JOIN (SELECT COUNT(*) as "Count", "ActionID" FROM "Item" GROUP BY "ActionID") AS "ICOUNT" ON "ICOUNT"."ActionID" = "Action"."ActionID" 
ORDER BY "TotalTime" DESC;


-- Show Reader / Processor / Writer Tiems for each step which has an item based processing (Won't show tasklets)
CREATE VIEW "Overview" AS 
SELECT "Job"."JobID", "Step"."StepID", "Action"."ActionType", "Job"."JobName" AS "Job", "Step"."StepName" AS "Step", "Step"."StepStart" AS "StepStart", "Step"."StepEnd" AS "StepEnd", "Action"."ActionName" AS "Action", sum("Item"."TimeInMS" ) as "Total", count("Item"."ItemID" ) as "ProcessedItems" from "Job" 
INNER JOIN "Step" ON "Step"."JobID" = "Job"."JobID"
INNER JOIN "ChunkExecution" ON "ChunkExecution"."StepID" = "Step"."StepID"
INNER JOIN "Item" ON "Item"."ChunkExecutionID" = "ChunkExecution"."ChunkExecutionID"
INNER JOIN "Action" ON "Action"."ActionID" = "Item"."ActionID"
GROUP BY "Step"."StepID", "Action"."ActionType", "Action"."ActionID"
ORDER BY "JobID","StepID","ActionType";

