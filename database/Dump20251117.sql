-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: dog
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `disease`
--

DROP TABLE IF EXISTS `disease`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disease` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disease`
--

LOCK TABLES `disease` WRITE;
/*!40000 ALTER TABLE `disease` DISABLE KEYS */;
/*!40000 ALTER TABLE `disease` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disease_related_symptom_ids`
--

DROP TABLE IF EXISTS `disease_related_symptom_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disease_related_symptom_ids` (
  `disease_id` bigint NOT NULL,
  `related_symptom_ids` bigint DEFAULT NULL,
  KEY `FKmqr8qiwafawkqvbmla5flvs0a` (`disease_id`),
  CONSTRAINT `FKmqr8qiwafawkqvbmla5flvs0a` FOREIGN KEY (`disease_id`) REFERENCES `disease` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disease_related_symptom_ids`
--

LOCK TABLES `disease_related_symptom_ids` WRITE;
/*!40000 ALTER TABLE `disease_related_symptom_ids` DISABLE KEYS */;
/*!40000 ALTER TABLE `disease_related_symptom_ids` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pet_place`
--

DROP TABLE IF EXISTS `pet_place`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pet_place` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pet_allowed` bit(1) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `type` enum('GROOMING','HOSPITAL','HOTEL','OTHER','PET_CAFE','PET_RESTAURANT','PET_SHOP') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pet_place`
--

LOCK TABLES `pet_place` WRITE;
/*!40000 ALTER TABLE `pet_place` DISABLE KEYS */;
/*!40000 ALTER TABLE `pet_place` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `place`
--

DROP TABLE IF EXISTS `place`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `place` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `place_url` varchar(255) DEFAULT NULL,
  `road_address` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `place`
--

LOCK TABLES `place` WRITE;
/*!40000 ALTER TABLE `place` DISABLE KEYS */;
/*!40000 ALTER TABLE `place` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recurrence_rule`
--

DROP TABLE IF EXISTS `recurrence_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recurrence_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `day_of_month` int DEFAULT NULL,
  `interval_value` int NOT NULL,
  `nth_week` int DEFAULT NULL,
  `repeat_count` int DEFAULT NULL,
  `type` enum('CUSTOM','DAILY','MONTHLY','WEEKLY') NOT NULL,
  `until_date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recurrence_rule`
--

LOCK TABLES `recurrence_rule` WRITE;
/*!40000 ALTER TABLE `recurrence_rule` DISABLE KEYS */;
INSERT INTO `recurrence_rule` VALUES (1,NULL,1,NULL,NULL,'DAILY',NULL),(2,NULL,1,NULL,NULL,'DAILY',NULL);
/*!40000 ALTER TABLE `recurrence_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recurrence_rule_dow`
--

DROP TABLE IF EXISTS `recurrence_rule_dow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recurrence_rule_dow` (
  `recurrence_rule_id` bigint NOT NULL,
  `day_of_week` enum('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') DEFAULT NULL,
  KEY `FKea80lx1l5glmwe19d8y2p41j4` (`recurrence_rule_id`),
  CONSTRAINT `FKea80lx1l5glmwe19d8y2p41j4` FOREIGN KEY (`recurrence_rule_id`) REFERENCES `recurrence_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recurrence_rule_dow`
--

LOCK TABLES `recurrence_rule_dow` WRITE;
/*!40000 ALTER TABLE `recurrence_rule_dow` DISABLE KEYS */;
/*!40000 ALTER TABLE `recurrence_rule_dow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reminder_log`
--

DROP TABLE IF EXISTS `reminder_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reminder_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message` varchar(255) DEFAULT NULL,
  `reminder_time` datetime(6) DEFAULT NULL,
  `success` bit(1) DEFAULT NULL,
  `schedule_instance_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK24hudtl66s79nkl9q4100nryn` (`schedule_instance_id`),
  CONSTRAINT `FK24hudtl66s79nkl9q4100nryn` FOREIGN KEY (`schedule_instance_id`) REFERENCES `schedule_instance` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reminder_log`
--

LOCK TABLES `reminder_log` WRITE;
/*!40000 ALTER TABLE `reminder_log` DISABLE KEYS */;
INSERT INTO `reminder_log` VALUES (1,'?강아지 산책 일정이 10분 후입니다! (2025-11-17T12:00)','2025-11-17 11:50:29.346290',_binary '',6),(2,'?사료 일정이 5분 후입니다! (2025-11-17T12:30)','2025-11-17 12:25:29.373382',_binary '',1);
/*!40000 ALTER TABLE `reminder_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_date` date DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  `recurring` bit(1) DEFAULT NULL,
  `remind_before_minutes` int DEFAULT NULL,
  `schedule_time` datetime(6) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `recurrence_rule_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKdjfhs40mr2a9xwdjibj9rymg2` (`recurrence_rule_id`),
  CONSTRAINT `FK6dmyeewaeqkxhg8mbgrc4jcwh` FOREIGN KEY (`recurrence_rule_id`) REFERENCES `recurrence_rule` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule`
--

LOCK TABLES `schedule` WRITE;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
INSERT INTO `schedule` VALUES (1,NULL,1,_binary '\0',5,'2025-11-17 12:30:00.000000',NULL,'사료',NULL),(2,'2025-11-20',1,_binary '',10,'2025-11-17 07:00:00.000000','2025-11-17','강아지 산책',1),(3,'2025-11-20',1,_binary '',10,'2025-11-17 12:00:00.000000','2025-11-17','강아지 산책',2);
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule_instance`
--

DROP TABLE IF EXISTS `schedule_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule_instance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `completed` bit(1) NOT NULL,
  `occurrence_time` datetime(6) NOT NULL,
  `schedule_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgwq7v3du8xka6ftd0g7px409j` (`schedule_id`),
  CONSTRAINT `FKgwq7v3du8xka6ftd0g7px409j` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule_instance`
--

LOCK TABLES `schedule_instance` WRITE;
/*!40000 ALTER TABLE `schedule_instance` DISABLE KEYS */;
INSERT INTO `schedule_instance` VALUES (1,_binary '','2025-11-17 12:30:00.000000',1),(2,_binary '','2025-11-17 07:00:00.000000',2),(3,_binary '\0','2025-11-18 07:00:00.000000',2),(4,_binary '\0','2025-11-19 07:00:00.000000',2),(5,_binary '\0','2025-11-20 07:00:00.000000',2),(6,_binary '','2025-11-17 12:00:00.000000',3),(7,_binary '\0','2025-11-18 12:00:00.000000',3),(8,_binary '\0','2025-11-19 12:00:00.000000',3),(9,_binary '\0','2025-11-20 12:00:00.000000',3);
/*!40000 ALTER TABLE `schedule_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `symptom`
--

DROP TABLE IF EXISTS `symptom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `symptom` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `member_id` bigint NOT NULL,
  `symptom_date` datetime(6) DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `symptom`
--

LOCK TABLES `symptom` WRITE;
/*!40000 ALTER TABLE `symptom` DISABLE KEYS */;
INSERT INTO `symptom` VALUES (1,'2025-11-21 17:07:09.232329','구토 후 식욕이 떨어짐',1,'2025-12-01 10:00:00.000000','구토 + 식욕 저하'),(2,'2025-11-24 09:51:12.059402','사료 섭취 뒤 구토. 물도 못마심',1,'2025-11-24 15:30:00.000000','강아지'),(3,'2025-11-24 14:03:43.992015','사료 섭취 뒤 구토. 물도 못마심',1,'2025-11-24 15:30:00.000000','강아지'),(4,'2025-11-27 15:56:27.031705','아침에 한 번 구토',1,'2025-12-01 09:00:00.000000','구토'),(5,'2025-12-02 15:37:13.956205','아침에 111한 번 구토',1,'2025-12-01 09:00:00.000000','ㅇㅇㅇㅇ55'),(7,'2025-12-02 15:42:26.174144','아침에 한 번 구토',1,'2025-12-01 14:30:00.000000','구토');
/*!40000 ALTER TABLE `symptom` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

--
-- Table structure for table `toxic_food`
--

DROP TABLE IF EXISTS `toxic_food`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `toxic_food` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` enum('ALCOHOL','ALLIUM','AVOCADO','CAFFEINE','CHOCOLATE','DAIRY','FRUIT','NUTS','OTHER','RAW_BONES','SWEETENERS','VEGETABLE') DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `toxicity_level` enum('DANGEROUS','FATAL','SAFE','WARNING') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `toxic_food`
--

LOCK TABLES `toxic_food` WRITE;
/*!40000 ALTER TABLE `toxic_food` DISABLE KEYS */;
/*!40000 ALTER TABLE `toxic_food` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-17 14:42:24
