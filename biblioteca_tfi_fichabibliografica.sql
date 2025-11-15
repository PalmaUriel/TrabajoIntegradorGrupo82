-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: biblioteca_tfi
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
-- Table structure for table `fichabibliografica`
--

DROP TABLE IF EXISTS `fichabibliografica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fichabibliografica` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `idLIBRO` bigint NOT NULL,
  `isbn` varchar(17) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `clasificacionDewey` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estanteria` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `idioma` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `eliminado` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idLIBRO` (`idLIBRO`),
  UNIQUE KEY `isbn` (`isbn`),
  KEY `idx_ficha_isbn` (`isbn`),
  CONSTRAINT `fk_ficha_libro` FOREIGN KEY (`idLIBRO`) REFERENCES `libro` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fichabibliografica`
--

LOCK TABLES `fichabibliografica` WRITE;
/*!40000 ALTER TABLE `fichabibliografica` DISABLE KEYS */;
INSERT INTO `fichabibliografica` VALUES (1,1,'978-84-376-0494-7','863.64','A3-B12','Español',0),(2,2,'978-987-566-623-0','843.91','A1-C05','Español',0),(3,3,'978-0-553-29335-7','813.54','B2-A07','Ingles',0),(4,4,'978-950-07-0243-1','863.64','A3-B13','Español',0),(5,5,'978-0-452-28423-4','823.91','B1-C10','Ingles',0),(6,6,'1234567898','100','12','esp',0),(7,7,'1326578947','100','16','esp',0),(8,8,'1234567891','100','12','Español',0),(9,9,'1234123454','200','4','Español',0);
/*!40000 ALTER TABLE `fichabibliografica` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-14 21:34:48
