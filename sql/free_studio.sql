CREATE DATABASE  IF NOT EXISTS `free_studio` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `free_studio`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: free_studio
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `attivita`
--

DROP TABLE IF EXISTS `attivita`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attivita` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_progetto` int DEFAULT NULL,
  `descrizione` varchar(200) DEFAULT NULL,
  `data_attivita` varchar(10) DEFAULT NULL,
  `ore_lavorate` double NOT NULL,
  `costo_orario` double NOT NULL,
  `note` text,
  `attivo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `id_progetto` (`id_progetto`),
  CONSTRAINT `attivita_ibfk_1` FOREIGN KEY (`id_progetto`) REFERENCES `progetto` (`id`),
  CONSTRAINT `attivita_chk_1` CHECK ((`ore_lavorate` >= 0)),
  CONSTRAINT `attivita_chk_2` CHECK ((`costo_orario` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attivita`
--

LOCK TABLES `attivita` WRITE;
/*!40000 ALTER TABLE `attivita` DISABLE KEYS */;
/*!40000 ALTER TABLE `attivita` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `cognome` varchar(50) NOT NULL,
  `ragione_sociale` varchar(100) DEFAULT NULL,
  `partita_iva` varchar(20) DEFAULT NULL,
  `codice_fiscale` varchar(20) DEFAULT NULL,
  `email` varchar(80) NOT NULL,
  `telefono` varchar(30) NOT NULL,
  `indirizzo` varchar(150) DEFAULT NULL,
  `citta` varchar(80) DEFAULT NULL,
  `cap` varchar(10) DEFAULT NULL,
  `provincia` varchar(20) DEFAULT NULL,
  `paese` varchar(50) DEFAULT NULL,
  `note` text,
  `data_creazione` varchar(10) DEFAULT NULL,
  `attivo` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,'Mario','Rossi','','','','mario@mail.com','4245622','','roma','','','','','2025-12-24',1),(2,'Laura','Bianchi','','','','laura@mail.it','1324534','','','','','','','2025-12-24',1),(3,'Paolo','Verdi','','','','paolo@mail.it','436546457','','','','','','','2025-12-24',1),(4,'Giulia','Neri','','','','giulia@mail.com','678679837869','','','','','','','2025-12-24',1),(5,'Studio','Alpha SRL','','','','studio@mail.it','324613461','','','','','','','2025-12-24',1);
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fattura`
--

DROP TABLE IF EXISTS `fattura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fattura` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int DEFAULT NULL,
  `numero_fattura` varchar(30) NOT NULL,
  `data_fattura` varchar(10) NOT NULL,
  `data_scadenza` varchar(10) NOT NULL,
  `importo` double NOT NULL,
  `stato_pagamento` varchar(30) DEFAULT NULL,
  `metodo_pagamento` varchar(30) DEFAULT NULL,
  `note` text,
  `attivo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `id_cliente` (`id_cliente`),
  CONSTRAINT `fattura_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id`),
  CONSTRAINT `fattura_chk_1` CHECK ((`importo` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fattura`
--

LOCK TABLES `fattura` WRITE;
/*!40000 ALTER TABLE `fattura` DISABLE KEYS */;
INSERT INTO `fattura` VALUES (1,1,'2025-001','2025-12-24','2025-12-29',1200.5,'Parziale',NULL,'',1),(2,2,'2025-002','2025-12-13','2025-12-26',3000,'Pagata',NULL,'',1),(3,3,'2025-003','2025-11-24','2025-12-19',5000,'Pagata',NULL,'',1),(4,4,'2025-004','2025-12-24','2026-01-24',300,'Non pagata',NULL,'',1),(5,5,'2025-005','2025-12-19','2026-01-03',8000,'Non pagata',NULL,'',1),(6,1,'2025-006','2025-12-08','2025-12-17',1000,'Non pagata',NULL,'',1);
/*!40000 ALTER TABLE `fattura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagamento`
--

DROP TABLE IF EXISTS `pagamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagamento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_fattura` int DEFAULT NULL,
  `data_pagamento` varchar(10) DEFAULT NULL,
  `importo_pagato` double NOT NULL,
  `metodo` varchar(30) DEFAULT NULL,
  `note` text,
  `attivo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `id_fattura` (`id_fattura`),
  CONSTRAINT `pagamento_ibfk_1` FOREIGN KEY (`id_fattura`) REFERENCES `fattura` (`id`),
  CONSTRAINT `pagamento_chk_1` CHECK ((`importo_pagato` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagamento`
--

LOCK TABLES `pagamento` WRITE;
/*!40000 ALTER TABLE `pagamento` DISABLE KEYS */;
INSERT INTO `pagamento` VALUES (1,2,'2025-12-24',1500,'Contanti','',1),(2,2,'2025-12-24',1500,'POS','',1),(3,3,'2025-12-21',5000,'Bonifico','',1),(4,1,'2025-12-24',600,'Contanti','',1);
/*!40000 ALTER TABLE `pagamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `progetto`
--

DROP TABLE IF EXISTS `progetto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `progetto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int DEFAULT NULL,
  `titolo` varchar(100) NOT NULL,
  `descrizione` text,
  `stato` varchar(30) DEFAULT NULL,
  `data_inizio` varchar(10) DEFAULT NULL,
  `data_fine` varchar(10) DEFAULT NULL,
  `preventivo` double DEFAULT NULL,
  `costo_effettivo` double DEFAULT NULL,
  `fatturabile` tinyint(1) DEFAULT NULL,
  `priorita` int DEFAULT NULL,
  `note` text,
  `attivo` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_cliente` (`id_cliente`),
  CONSTRAINT `progetto_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `progetto`
--

LOCK TABLES `progetto` WRITE;
/*!40000 ALTER TABLE `progetto` DISABLE KEYS */;
INSERT INTO `progetto` VALUES (1,1,'sito web','','In corso','2025-12-24','2026-01-04',1200.5,0,1,3,'',1),(2,2,'app mobile','','In corso','2025-12-13','2025-12-26',3000,1800,1,5,'',1),(3,3,'gestionale','','Completato','2025-11-24','2025-12-22',5000,5100,1,4,'',1),(4,4,'logo','','In corso','2025-12-24',NULL,300,0,0,2,'',1),(5,5,'CRM','','Sospeso','2025-12-25','2026-01-24',8000,0,1,5,'',1);
/*!40000 ALTER TABLE `progetto` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-24 17:17:44
