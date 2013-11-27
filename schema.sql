CREATE DATABASE  IF NOT EXISTS `city_game` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `city_game`;
-- MySQL dump 10.13  Distrib 5.6.13, for osx10.6 (i386)
--
-- Host: 127.0.0.1    Database: city_game
-- ------------------------------------------------------
-- Server version	5.6.14

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
-- Table structure for table `Checkpoints`
--

DROP TABLE IF EXISTS `Checkpoints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Checkpoints` (
  `idCheckpoint` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL COMMENT '	',
  `message` varchar(160) NOT NULL,
  `points` int(11) NOT NULL,
  PRIMARY KEY (`idCheckpoint`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Checkpoints`
--

LOCK TABLES `Checkpoints` WRITE;
/*!40000 ALTER TABLE `Checkpoints` DISABLE KEYS */;
/*!40000 ALTER TABLE `Checkpoints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CheckpointsAnswers`
--

DROP TABLE IF EXISTS `CheckpointsAnswers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CheckpointsAnswers` (
  `idCheckpointAnswer` int(11) NOT NULL AUTO_INCREMENT,
  `idCheckpoint` int(11) NOT NULL,
  `answer` varchar(160) NOT NULL,
  PRIMARY KEY (`idCheckpointAnswer`),
  KEY `FK_CheckpointsAnswers_Checkpoint_idx` (`idCheckpoint`),
  CONSTRAINT `FK_CheckpointsAnswers_Checkpoint` FOREIGN KEY (`idCheckpoint`) REFERENCES `Checkpoints` (`idCheckpoint`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CheckpointsAnswers`
--

LOCK TABLES `CheckpointsAnswers` WRITE;
/*!40000 ALTER TABLE `CheckpointsAnswers` DISABLE KEYS */;
/*!40000 ALTER TABLE `CheckpointsAnswers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Games`
--

DROP TABLE IF EXISTS `Games`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Games` (
  `idGame` int(11) NOT NULL AUTO_INCREMENT,
  `idPlayer` int(11) NOT NULL,
  `idScenario` int(11) NOT NULL,
  `idStatus` int(11) NOT NULL,
  `startDate` datetime NOT NULL,
  `points` int(11) NOT NULL,
  PRIMARY KEY (`idGame`),
  KEY `FK_Player_idx` (`idPlayer`),
  KEY `FK_Scenario_idx` (`idScenario`),
  KEY `FK_Status_idx` (`idStatus`),
  CONSTRAINT `FK_Player` FOREIGN KEY (`idPlayer`) REFERENCES `Users` (`idUser`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_Scenario` FOREIGN KEY (`idScenario`) REFERENCES `Scenarios` (`idScenario`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_Status` FOREIGN KEY (`idStatus`) REFERENCES `Statuses` (`idStatus`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Games`
--

LOCK TABLES `Games` WRITE;
/*!40000 ALTER TABLE `Games` DISABLE KEYS */;
/*!40000 ALTER TABLE `Games` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GamesVisitedCheckpoints`
--

DROP TABLE IF EXISTS `GamesVisitedCheckpoints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GamesVisitedCheckpoints` (
  `idGameVisitedCheckpoint` int(11) NOT NULL AUTO_INCREMENT,
  `idGame` int(11) NOT NULL,
  `idCheckpoint` int(11) NOT NULL,
  PRIMARY KEY (`idGameVisitedCheckpoint`),
  KEY `FK_GamesVisitedCheckpoints_Game_idx` (`idGame`),
  KEY `FK_GamesVisitedCheckpoints_Checkpoint_idx` (`idCheckpoint`),
  CONSTRAINT `FK_GamesVisitedCheckpoints_Game` FOREIGN KEY (`idGame`) REFERENCES `Games` (`idGame`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_GamesVisitedCheckpoints_Checkpoint` FOREIGN KEY (`idCheckpoint`) REFERENCES `Checkpoints` (`idCheckpoint`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GamesVisitedCheckpoints`
--

LOCK TABLES `GamesVisitedCheckpoints` WRITE;
/*!40000 ALTER TABLE `GamesVisitedCheckpoints` DISABLE KEYS */;
/*!40000 ALTER TABLE `GamesVisitedCheckpoints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Privileges`
--

DROP TABLE IF EXISTS `Privileges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Privileges` (
  `idPrivilege` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`idPrivilege`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Privileges`
--

LOCK TABLES `Privileges` WRITE;
/*!40000 ALTER TABLE `Privileges` DISABLE KEYS */;
/*!40000 ALTER TABLE `Privileges` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Scenarios`
--

DROP TABLE IF EXISTS `Scenarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Scenarios` (
  `idScenario` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `isPublic` bit(1) NOT NULL,
  `isAccepted` bit(1) NOT NULL,
  `expirationDate` datetime DEFAULT NULL,
  PRIMARY KEY (`idScenario`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Scenarios`
--

LOCK TABLES `Scenarios` WRITE;
/*!40000 ALTER TABLE `Scenarios` DISABLE KEYS */;
/*!40000 ALTER TABLE `Scenarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ScenariosCheckpoints`
--

DROP TABLE IF EXISTS `ScenariosCheckpoints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ScenariosCheckpoints` (
  `idScenarioCheckpoint` int(11) NOT NULL AUTO_INCREMENT,
  `idScenario` int(11) NOT NULL,
  `idCheckpoint` int(11) NOT NULL,
  PRIMARY KEY (`idScenarioCheckpoint`),
  KEY `FK_ScenarioCheckpoints_Scenario_idx` (`idScenario`),
  KEY `FK_ScenariosCheckpoints_Checkpoint_idx` (`idCheckpoint`),
  CONSTRAINT `FK_ScenariosCheckpoints_Scenario` FOREIGN KEY (`idScenario`) REFERENCES `Scenarios` (`idScenario`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_ScenariosCheckpoints_Checkpoint` FOREIGN KEY (`idCheckpoint`) REFERENCES `Checkpoints` (`idCheckpoint`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ScenariosCheckpoints`
--

LOCK TABLES `ScenariosCheckpoints` WRITE;
/*!40000 ALTER TABLE `ScenariosCheckpoints` DISABLE KEYS */;
/*!40000 ALTER TABLE `ScenariosCheckpoints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Statuses`
--

DROP TABLE IF EXISTS `Statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Statuses` (
  `idStatus` int(11) NOT NULL,
  `name` varchar(10) NOT NULL,
  PRIMARY KEY (`idStatus`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Statuses`
--

LOCK TABLES `Statuses` WRITE;
/*!40000 ALTER TABLE `Statuses` DISABLE KEYS */;
/*!40000 ALTER TABLE `Statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `idUser` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(254) NOT NULL,
  `passwordHash` char(60) NOT NULL,
  `alias` varchar(20) NOT NULL,
  PRIMARY KEY (`idUser`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `alias_UNIQUE` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UsersGames`
--

DROP TABLE IF EXISTS `UsersGames`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UsersGames` (
  `idUserGame` int(11) NOT NULL AUTO_INCREMENT,
  `idUser` int(11) NOT NULL,
  `idGame` int(11) NOT NULL,
  PRIMARY KEY (`idUserGame`),
  KEY `FK_UsersGames_Game_idx` (`idGame`),
  KEY `FK_UsersGames_User_idx` (`idUser`),
  CONSTRAINT `FK_UsersGames_Game` FOREIGN KEY (`idGame`) REFERENCES `Games` (`idGame`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_UsersGames_User` FOREIGN KEY (`idUser`) REFERENCES `Users` (`idUser`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UsersGames`
--

LOCK TABLES `UsersGames` WRITE;
/*!40000 ALTER TABLE `UsersGames` DISABLE KEYS */;
/*!40000 ALTER TABLE `UsersGames` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UsersPrivileges`
--

DROP TABLE IF EXISTS `UsersPrivileges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UsersPrivileges` (
  `idUserPrivilege` int(11) NOT NULL AUTO_INCREMENT,
  `idPrivilege` int(11) NOT NULL,
  `idUser` int(11) NOT NULL,
  PRIMARY KEY (`idUserPrivilege`),
  KEY `FK_Privilege_idx` (`idPrivilege`),
  KEY `FK_User_idx` (`idUser`),
  CONSTRAINT `FK_UsersPrivileges_User` FOREIGN KEY (`idUser`) REFERENCES `Users` (`idUser`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_UsersPrivileges_Privilege` FOREIGN KEY (`idPrivilege`) REFERENCES `Privileges` (`idPrivilege`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UsersPrivileges`
--

LOCK TABLES `UsersPrivileges` WRITE;
/*!40000 ALTER TABLE `UsersPrivileges` DISABLE KEYS */;
/*!40000 ALTER TABLE `UsersPrivileges` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UsersScenarios`
--

DROP TABLE IF EXISTS `UsersScenarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UsersScenarios` (
  `idUsersScenario` int(11) NOT NULL AUTO_INCREMENT,
  `idUser` int(11) NOT NULL,
  `idScenario` int(11) NOT NULL,
  `isOwner` bit(1) NOT NULL,
  PRIMARY KEY (`idUsersScenario`),
  KEY `UserFK_idx` (`idUser`),
  KEY `ScenarioFK_idx` (`idScenario`),
  CONSTRAINT `FK_UsersScenario_User` FOREIGN KEY (`idUser`) REFERENCES `Users` (`idUser`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_UsersScenario_Scenario` FOREIGN KEY (`idScenario`) REFERENCES `Scenarios` (`idScenario`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UsersScenarios`
--

LOCK TABLES `UsersScenarios` WRITE;
/*!40000 ALTER TABLE `UsersScenarios` DISABLE KEYS */;
/*!40000 ALTER TABLE `UsersScenarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-11-27 19:18:56
