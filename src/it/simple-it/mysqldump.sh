#!/usr/bin/env bash

echo "-- THIS DUMP IS JUST EMULATION"
echo "-- You need mysqldump be installed to run this plugin"

if [[ $* == *-Q* ]]
    then
        echo "-- quoteNames: enabled"
    else
        echo "-- quoteNames: disabled"
fi

if [[ $* == *-c* ]]
    then
        echo "-- completeInsert: enabled"
    else
        echo "-- completeInsert: disabled"
fi

if [[ $* == *-e* ]]
    then
        echo "-- extendedInsert: enabled"
    else
        echo "-- extendedInsert: disabled"
fi

if [[ $* == *--single-transaction* ]]
    then
        echo "-- singleTransaction: enabled"
    else
        echo "-- singleTransaction: disabled"
fi

for argument; do
    key=${argument%%=*}
    value=${argument#*=}

    if [[ key != -* ]]
        then
            database=${key};
    fi

    case "$key" in
          --user)   user=${value};;
      --password)   password=${value};;
          --host)   host=${value};;
          --port)   port=${value};;
    esac
done


date=`date '+%Y-%m-%d %H:%M:%S'`

dumpexample="
--
-- User: $user    Password: $password
-- Host: $host    Database: $database
-- ------------------------------------------------------
-- Server version	5.7.24-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `company_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `company_code` varchar(5) NOT NULL,
  PRIMARY KEY (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` (`company_id`, `name`, `company_code`) VALUES (1,'Amazon','AMZN'),(2,'Oracle','ORCL'),(3,'Alphabet Inc ','GOOGL'),(4,'Microsoft','MSFT'),(5,'IBM','IBM');
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on $date"

echo "$dumpexample"


