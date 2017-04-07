CREATE TABLE `Customer` (
  `CustomerID` INT NOT NULL,
  `FirstName` VARCHAR(100) NOT NULL,
  `LastName` VARCHAR(100) NOT NULL,
  `TransactionTotal` FLOAT NULL DEFAULT 0,
  PRIMARY KEY (`CustomerID`));


  CREATE TABLE `Transaction` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CustomerID` INT NOT NULL,
  `Amount` INT NULL,
  PRIMARY KEY (`ID`));
 
 
TRUNCATE TABLE Customer;
TRUNCATE TABLE Transaction;


CREATE TABLE `CustomerUpperCase` (
  `CustomerID` INT NOT NULL,
  `FirstName` VARCHAR(100) NOT NULL,
  `LastName` VARCHAR(100) NOT NULL,
  `TransactionTotal` FLOAT NULL DEFAULT 0,
  PRIMARY KEY (`CustomerID`));
  

  CREATE TABLE `CustomerLowerCase` (
  `CustomerID` INT NOT NULL,
  `FirstName` VARCHAR(100) NOT NULL,
  `LastName` VARCHAR(100) NOT NULL,
  `TransactionTOtal` FLOAT NULL DEFAULT 0,
  PRIMARY KEY (`CustomerID`));