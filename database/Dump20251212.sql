-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: petdb
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
-- Table structure for table `breed`
--

DROP TABLE IF EXISTS `breed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `breed` (
  `breed_id` int NOT NULL AUTO_INCREMENT,
  `breedname` varchar(50) NOT NULL,
  PRIMARY KEY (`breed_id`),
  UNIQUE KEY `UQ_breedname` (`breedname`)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `breed`
--

LOCK TABLES `breed` WRITE;
/*!40000 ALTER TABLE `breed` DISABLE KEYS */;
INSERT INTO `breed` VALUES (116,'골든 리트리버'),(25,'그레이트 덴'),(140,'그레이하운드'),(132,'꼬똥 드 툴레아'),(33,'네오폴리탄 마스티프'),(81,'노르위전 엘크하운드'),(58,'노리치 테리어'),(114,'노바 스코샤 덕 톨링 리트리버'),(57,'노퍽 테리어'),(32,'뉴펀들랜드'),(68,'닥스훈트'),(94,'달마시안'),(49,'댄디 딘몬트 테리어'),(22,'도고 아르헨티노'),(23,'도베르만'),(37,'도사'),(139,'디어하운드'),(126,'라사 압소'),(105,'라지 먼스터랜더'),(113,'래브라도 리트리버'),(7,'러프 콜리'),(28,'레온베르거'),(54,'레이크랜드 테리어'),(96,'로디지안 리즈백'),(34,'로트바일러'),(134,'리틀 라이언 독'),(29,'마스티프'),(127,'말티즈'),(56,'맨체스터 테리어'),(55,'미니어쳐 불 테리어'),(31,'미니어쳐 슈나우저'),(30,'미니어쳐 핀셔'),(71,'바센지'),(92,'바셋 하운드'),(107,'바이마라너'),(20,'버니즈 마운틴 독'),(46,'베들링턴 테리어'),(6,'벨지안 셰퍼드 독'),(4,'보더 콜리'),(138,'보르조이'),(120,'보스턴 테리어'),(21,'복서'),(5,'부비에 데 플랑드르'),(47,'불 테리어'),(18,'불독'),(19,'불마스티프'),(119,'브뤼셀 그리폰'),(99,'브리타니 스파니엘'),(93,'블랙 앤 탄 쿤 하운드'),(97,'블러드하운드'),(91,'비글'),(118,'비숑 프리제'),(3,'비어디드 콜리'),(106,'비즐라'),(130,'빠삐용'),(95,'쁘띠 바셋 그리폰 벤딘'),(84,'사모예드'),(143,'살루키'),(40,'샤페이'),(35,'세인트 버나드'),(41,'센트럴 아시아 셰퍼드독'),(13,'셰틀랜드 쉽독'),(36,'슈나우저'),(8,'스무스 콜리'),(60,'스무스 폭스 테리어'),(62,'스카이 테리어'),(63,'스코티쉬 테리어'),(78,'시바'),(74,'시베리안 허스키'),(131,'시츄'),(79,'시코쿠'),(61,'실리햄 테리어'),(44,'아메리칸 스태포드셔 테리어'),(73,'아메리칸 아키타'),(109,'아메리칸 코커 스파니엘'),(108,'아이리쉬 레드 세터'),(104,'아이리쉬 레드 앤 화이트 세터'),(141,'아이리쉬 울프하운드'),(112,'아이리쉬 워터 스파니엘'),(51,'아이리쉬소프트 코티드 휘튼 테리어'),(145,'아자와크'),(76,'아키타'),(17,'아펜핀셔'),(137,'아프간 하운드'),(70,'알라스칸 말라뮤트'),(45,'에어데일 테리어'),(2,'오스트레일리언 셰퍼드'),(1,'오스트레일리언 캐틀 독'),(10,'올드 잉글리쉬 쉽독'),(64,'와이어 폭스 테리어'),(67,'요크셔 테리어'),(88,'웨스트 시베리안 라이카'),(66,'웨스트 하일랜드 화이트 테리어'),(14,'웰시 코기 카디건'),(15,'웰시 코기 펨브로크'),(65,'웰시 테리어'),(87,'이스트 시베리안 라이카'),(142,'이탈리언 사이트하운드'),(101,'잉글리쉬 세터'),(117,'잉글리쉬 스프링거 스파니엘'),(110,'잉글리쉬 코커 스파니엘'),(100,'잉글리쉬 포인터'),(27,'자이언트 슈나우저'),(85,'재패니즈 스피츠'),(52,'잭 러셀 테리어'),(12,'저먼 셰퍼드 독'),(102,'저먼 숏 헤어드 포인팅 독'),(90,'저먼 스피츠'),(103,'저먼 와이어 헤어드 포인팅 독'),(50,'저먼 헌팅 테리어'),(136,'제패니즈 친'),(75,'진돗개'),(72,'차우차우'),(121,'차이니즈 크레스티드 독'),(16,'체코슬로바키안 울프독'),(122,'치와와'),(124,'캐벌리어 킹 찰스 스파니엘'),(53,'케리 블루 테리어'),(48,'케언 테리어'),(39,'케인 코르소 이탈리아노'),(9,'코몬도르'),(42,'코카시안 셰퍼드독'),(77,'키슈'),(80,'키스혼드'),(125,'킹 찰스 스파니엘'),(86,'타이 리지백 독'),(89,'타이완 독'),(38,'티베탄 마스티프'),(133,'티베탄 테리어'),(83,'파라오 하운드'),(59,'파슨 러셀 테리어'),(128,'퍼그'),(129,'페키니즈'),(115,'포르투기즈 워터 독'),(82,'포메라니언'),(135,'푸들'),(11,'풀리'),(24,'프레사 까나리오'),(123,'프렌치 불독'),(111,'플랫 코티드 리트리버'),(26,'피레니언 마운틴 독'),(43,'필라 브라질레이로'),(98,'해리어'),(69,'호카이도'),(144,'휘핏');
/*!40000 ALTER TABLE `breed` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Table structure for table `food`
--

DROP TABLE IF EXISTS `food`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food` (
  `food_id` int NOT NULL AUTO_INCREMENT,
  `foodname` varchar(100) NOT NULL,
  `edible` tinyint(1) NOT NULL,
  `description` text,
  `cautions` text,
  PRIMARY KEY (`food_id`),
  UNIQUE KEY `UQ_foodname` (`foodname`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food`
--

LOCK TABLES `food` WRITE;
/*!40000 ALTER TABLE `food` DISABLE KEYS */;
INSERT INTO `food` VALUES (1,'캐슈넛',1,'다른 견과류에 비해 독성 물질이 없으며, 소량은 괜찮은 간식이 될 수 있습니다.','지방 함량이 높기 때문에 너무 많이 먹으면 체중 증가나 췌장염을 유발할 수 있습니다. 반드시 소금이 첨가되지 않은 제품만 한번에 소량만 주세요.'),(2,'치즈',1,'소량의 치즈는 훌륭한 간식으로 활용할 수 있습니다.','치즈는 지방 함량이 높고 유당을 포함하고 있어, 유당 불내증이 있는 강아지에게는 소화 문제를 일으킬 수 있습니다. 지방이 적은 종류(예: 모차렐라, 코티지 치즈)를 소량만 주는 것이 좋습니다.'),(3,'코코넛',1,'라우르산이 함유되어 면역 체계 강화와 구취 제거에 도움을 줄 수 있습니다.','오일 성분이 많아 과다 섭취 시 배탈이나 설사를 유발할 수 있으므로 소량만 급여해야 합니다. 껍질은 주지 마세요.'),(4,'옥수수',1,'적당량은 비타민, 미네랄, 단백질의 좋은 공급원입니다.','절대로 \'옥수수대(심지)\'를 주어서는 안 됩니다. 소화되지 않아 장 폐색을 일으킬 수 있습니다. 알맹이만 급여하세요.'),(5,'달걀',1,'훌륭한 단백질 공급원이며, 배탈이 났을 때 소화하기 좋은 음식입니다.','날달걀은 살모넬라균 위험과 비오틴 결핍을 유발할 수 있으므로 반드시 완전히 익혀서 급여해야 합니다.'),(6,'생선',1,'연어, 정어리 등은 단백질과 오메가-3 지방산이 풍부하여 털과 피부 건강에 좋습니다.','반드시 익혀서 급여해야 하며, 가시를 완벽히 제거해야 합니다. 기름이나 양념 없이 조리하세요.'),(7,'꿀',1,'비타민 A, B, C, D, K, 칼륨, 칼슘 등이 풍부하며 알레르기 완화에 도움을 줄 수 있습니다.','당분이 높으므로 비만견이나 당뇨가 있는 강아지에게는 위험합니다. 1세 미만의 강아지에게는 보툴리누스 중독 위험으로 급여하지 마세요.'),(8,'우유',1,'칼슘과 단백질이 풍부합니다.','많은 강아지들이 유당 불내증(Lactose Intolerance)이 있어 설사나 구토를 할 수 있습니다. 아주 소량만 주거나 락토 프리 우유를 권장합니다.'),(9,'땅콩버터',1,'단백질과 건강한 지방, 비타민 B와 E가 풍부하여 강아지들이 좋아하는 간식입니다.','반드시 \'자일리톨(Xylitol)\'이 없는지 확인해야 합니다(독성). 무염, 무설탕 제품을 선택하세요.'),(10,'팝콘',1,'리보플라빈과 티아민이 들어있어 눈 건강과 소화에 도움을 줄 수 있습니다.','버터와 소금이 없는 \'에어 프라이\' 팝콘만 가능합니다. 튀겨지지 않은 알갱이는 질식 위험이 있으니 제거하세요.'),(11,'돼지고기',1,'소화가 잘 되는 단백질원이며 아미노산이 풍부합니다.','지방이 많은 부위는 췌장염을 유발할 수 있습니다. 양념 없이 살코기만 완전히 익혀서 급여하세요.'),(12,'퀴노아',1,'옥수수나 밀 알레르기가 있는 강아지에게 훌륭한 대체 곡물이며 단백질이 풍부합니다.','사포닌 성분이 장을 자극할 수 있으므로 조리 전 깨끗이 씻어야 하며, 반드시 익혀서 줘야 합니다.'),(13,'새우',1,'비타민 B12, 니아신, 인이 풍부하고 지방이 적습니다.','반드시 껍질, 머리, 꼬리를 제거하고 완전히 익혀야 합니다. 콜레스테롤이 높으므로 가끔 소량만 주세요.'),(14,'칠면조',1,'단백질과 리보플라빈, 인이 풍부한 좋은 식재료입니다.','껍질과 지방을 제거한 살코기만 줘야 합니다. 특히 추수감사절 요리처럼 양념(마늘, 양파 등)된 고기는 절대 주지 마세요.'),(15,'요거트',1,'칼슘과 단백질이 풍부하며 유산균이 소화를 돕습니다.','설탕이나 인공 감미료(특히 자일리톨)가 들어가지 않은 플레인 요거트만 가능합니다. 유당 불내증이 있다면 피하세요.'),(16,'알코올',0,'강아지의 간과 뇌에 치명적인 손상을 입힙니다.','소량으로도 구토, 호흡 곤란, 혼수 상태, 심지어 사망에 이를 수 있습니다. 절대 금지입니다.'),(17,'아몬드',0,'강아지가 소화하기 어려우며 위장 장애를 일으킬 수 있습니다.','목에 걸려 질식할 위험이 크고, 높은 지방 함량은 췌장염을 유발할 수 있습니다.'),(18,'아보카도',0,'페르신 이라는 독소가 들어있어 구토와 설사를 유발합니다.','과육뿐만 아니라 껍질과 잎도 위험하며, 커다란 씨앗은 장 폐색의 주요 원인입니다.'),(19,'초콜릿',0,'테오브로민과 카페인이 들어있어 강아지의 대사 과정을 방해합니다.','구토, 설사, 불규칙한 심박수, 발작, 사망을 초래할 수 있습니다. 카카오 함량이 높을수록(다크 초콜릿) 더 위험합니다.'),(20,'계피',0,'입안을 자극하고 붓게 만들 수 있으며, 저혈당을 유발할 수 있습니다.','가루를 흡입하면 기침과 호흡 곤란을 일으키며, 심박수 변화나 간 질환을 유발할 수 있습니다.'),(21,'커피/카페인',0,'메틸잔틴 성분이 강아지의 신경계를 자극하여 매우 위험합니다.','소량 섭취로도 구토, 심계항진, 떨림, 발작을 일으키며 사망에 이를 수 있습니다.'),(22,'마늘',0,'적혈구를 파괴하여 빈혈을 유발합니다. 양파보다 독성이 5배 더 강합니다.','구운 것, 생 것, 가루 형태 등 모든 조리 방식에서 위험합니다. 섭취 후 며칠 뒤에 증상이 나타날 수 있습니다.'),(23,'포도/건포도',0,'강아지에게 급성 신부전(신장 망가짐)을 일으키는 매우 독성 강한 과일입니다.','어떤 강아지에게는 단 몇 알만으로도 치명적일 수 있습니다. 껍질을 벗겨도 절대 주면 안 됩니다.'),(24,'아이스크림',0,'다량의 설탕과 유당은 강아지에게 비만과 소화 불량을 일으킵니다.','일부 제품엔 독성 물질인 자일리톨이나 초콜릿이 포함될 수 있어 매우 위험합니다. 강아지 전용 아이스크림을 이용하세요.'),(25,'마카다미아',0,'강아지에게 신경계 독성을 일으키는 가장 위험한 견과류 중 하나입니다.','뒷다리 힘 풀림(마비), 구토, 고열, 떨림 증상이 나타납니다. 소량 섭취 시에도 즉시 병원에 가야 합니다.'),(26,'양파',0,'적혈구를 산화시켜 파괴하여 심각한 빈혈을 초래합니다.','짜장면 속 양파, 양파링 과자, 양파 가루 등 모든 형태가 위험합니다. 소변 색이 붉게 변하면 즉시 병원에 가세요.'),(27,'자일리톨',0,'인슐린을 급격하게 분비시켜 저혈당 쇼크와 간부전을 일으킵니다.','껌, 사탕, 치약, 다이어트 식품 등에 들어있습니다. 아주 적은 양으로도 생명을 위협하는 응급 상황이 발생합니다.'),(28,'체리',0,'씨앗, 줄기, 잎에 시안화물(청산가리 성분)이 함유되어 있습니다.','청산 중독으로 호흡 곤란을 일으킬 수 있으며, 씨앗은 장을 막을 수 있습니다.'),(29,'빵',1,'일반적인 플레인 빵은 소량 섭취 시 해롭지 않으나, 영양가는 없고 칼로리만 높습니다.','반드시 구워진 빵, 건포도 등 별도의 첨가물 없는 빵만 급여 가능합니다. 생 반죽은 위에서 팽창하고 알코올을 생성하므로 생명을 위협할 수 있습니다.'),(30,'당근',1,'베타카로틴과 비타민 A가 풍부하며, 딱딱한 식감이 치석 제거에 도움을 줍니다.','익히지 않은 당근은 소화되지 않고 그대로 배설될 수 있습니다. 목에 걸리지 않게 작게 잘라주세요.'),(31,'셀러리',1,'비타민 A, B, C가 풍부하고 아삭한 식감이 입 냄새 제거에 효과적입니다.','줄기의 질긴 섬유질 부분은 소화가 어렵고 질식 위험이 있으므로 아주 잘게 잘라서 급여하세요.'),(32,'그린빈',1,'칼로리가 낮고 철분과 비타민이 가득 차 있어 다이어트 간식으로 좋습니다.','사람용 통조림 제품은 나트륨 함량이 높으므로 피하고, 신선한 것을 익혀서 주세요.'),(33,'콜리플라워',1,'항산화 물질과 비타민이 풍부하며 칼로리가 낮아 면역력 강화에 좋습니다.','다량 섭취 시 가스를 유발하거나 배탈이 날 수 있으므로 적당량만 급여하세요.'),(34,'호박',1,'섬유질이 풍부하여 강아지의 소화 불량, 설사, 변비 개선에 탁월한 효과가 있습니다.','첨가물이 없는 100% 퓨레나 찐 호박만 가능합니다. 호박 파이 필링(설탕, 향신료 포함)은 절대 금지입니다.'),(35,'사과',1,'비타민 A, C와 섬유질이 풍부하여 피로 회복과 치아 건강에 좋습니다.','씨앗에는 시안화물(독성)이 있으므로 반드시 씨와 심지를 완벽히 제거하고 과육만 주세요.'),(36,'바나나',1,'칼륨, 비타민, 비오틴, 구리가 풍부하지만 당분이 다른 과일보다 높습니다.','당분과 칼로리가 높아 비만이나 당뇨가 있는 강아지에게는 아주 소량만 주세요. 껍질은 질겨서 소화되지 않습니다.'),(37,'오이',1,'수분 함량이 매우 높고 탄수화물, 지방이 거의 없어 비만견에게 최고의 간식입니다.','급하게 먹다 목에 걸릴 수 있으므로 한입 크기로 잘라주세요. 과식하면 묽은 변을 볼 수 있습니다.'),(38,'파인애플',1,'브로멜라인 효소가 들어있어 단백질 소화 흡수를 돕습니다.','당분이 매우 높고 산성이 강하므로 소량만 가끔 급여하세요. 껍질과 심지는 질식 위험이 있으니 제거해야 합니다.'),(39,'멜론',1,'수분 함량이 높고 비타민이 풍부하여 여름철 수분 보충에 좋습니다.','당도가 높으니 비만이나 당뇨가 있다면 주의하세요. 껍질과 씨앗은 소화되지 않으니 반드시 제거하고 과육만 줘야 합니다.'),(40,'블루베리',1,'항산화 성분, 섬유질, 비타민 C가 풍부한 슈퍼푸드입니다.','크기가 작아 그냥 삼키면 질식 위험이 있으니, 소형견에게는 으깨주거나 잘라주세요. 많이 먹으면 배탈이 날 수 있습니다.'),(41,'딸기',1,'비타민 C와 항산화제가 풍부하며, 치아 미백에 도움을 주는 효소도 들어있습니다.','당분이 높으므로 소량만 급여하세요. 꼭지 부분은 소화가 잘 안 되므로 제거하고 과육만 줘야 합니다.'),(42,'키위',1,'비타민 C와 칼륨이 풍부하여 면역력 강화와 호흡기 건강에 도움을 줍니다.','껍질은 질겨서 소화가 안 되므로 반드시 깎아서 주세요. 씨앗은 먹어도 되지만 다량 섭취 시 설사를 할 수 있습니다.'),(43,'베이컨',0,'높은 나트륨과 지방 함량으로 소화불량, 염증, 심장 질환 등을 유발할 수 있습니다.','가공육은 염분이 많아 췌장염을 일으킬 수 있으므로 아주 소량도 피하는 것이 좋습니다.'),(44,'감자',1,'비타민 C, B6, 철분 등 미네랄을 공급하며 탄수화물이 풍부하여 에너지원이 될 수 있습니다.','반드시 껍질을 벗겨 깨끗이 익혀서 급여해야 합니다. 싹이 나거나 푸른 부분에는 독성 물질인 \'솔라닌\'이 있으므로 절대 금지입니다.'),(45,'배',1,'수분과 식이섬유가 풍부하여 소화에 도움을 주고 변비 예방에 효과적입니다.','씨앗에는 소량의 시안화물(청산가리)이 포함되어 있어 반드시 제거해야 합니다. 당분이 있어 한 번에 너무 많은 양을 주지 마세요.'),(46,'레몬',0,'구연산과 에센셜 오일 성분이 강아지의 위장을 심하게 자극하여 문제를 일으킬 수 있습니다.','산성이 강하고 독성 물질이 포함되어 구토, 설사, 과민 반응을 유발할 수 있으므로 절대 주지 마세요.'),(47,'참외',1,'수분 함량이 높아 여름철 탈수 예방에 좋으며 비타민 C가 풍부합니다.','씨앗이 붙어 있는 부분(태좌)은 소화 불량이나 설사를 유발할 수 있고 당분이 높으므로, 제거 후 과육만 소량 급여하는 것이 안전합니다.'),(48,'토마토',1,'리코펜을 포함한 항산화 성분이 풍부합니다. 완전히 익은 빨간 과육은 대체로 안전합니다.','잎, 줄기, 그리고 푸른(익지 않은) 토마토에는 독성 물질인 \'솔라닌\'과 \'토마틴\'이 함유되어 있으므로 절대 주지 마세요.'),(49,'고추',0,'캡사이신 성분이 강아지의 위와 장을 심하게 자극하며 소화 기관에 통증과 염증을 유발합니다.','매운 고추는 물론 매운맛이 강한 모든 종류의 고추, 고춧가루가 들어간 음식은 강아지에게 주지 마세요.'),(50,'고구마',1,'비타민 A, C, B6, 칼륨 등 영양소가 풍부하고 소화가 잘 되는 섬유질이 많습니다.','반드시 껍질을 벗겨 깨끗하게 익혀서 주어야 합니다. 생 고구마는 소화가 어렵습니다. 칼로리가 높으니 과식을 주의하세요.'),(51,'브로콜리',1,'비타민 K, C, 섬유질이 풍부하고 항염 효과가 있습니다.','다량 섭취 시 이소티오시아네이트 성분이 위장 자극(가스, 설사)을 유발할 수 있습니다. 강아지 체중의 10% 이하 소량만 익혀서 급여해야 합니다.'),(52,'수박',1,'수분 함량이 90% 이상으로 여름철 탈수 예방에 매우 효과적입니다.','껍질과 씨앗은 소화 문제를 일으키거나 장을 막을 수 있으므로 모두 제거하고 빨간 과육만 급여해야 합니다.'),(53,'무',1,'비타민 C, 칼륨이 풍부하고 소화 효소가 있어 소화에 도움을 줍니다.','소량은 익히거나 생으로 주어도 되나, 너무 많은 양을 주면 가스를 유발할 수 있습니다. 작게 잘라 주세요.'),(54,'비트',1,'철분, 비타민 C, 식이섬유가 풍부하여 건강에 도움이 될 수 있습니다.','소량만 급여해야 하며, 급여 후 소변 색이 붉게 변할 수 있으나 정상적인 현상입니다. 옥살산염이 있어 신장 결석이 있는 강아지는 피하세요.'),(55,'버섯',1,'마트에서 파는 식용 버섯은 비타민 D, B를 포함하며 대부분 안전합니다.','야생 버섯은 독성 여부 판단이 불가능하고 매우 치명적일 수 있으므로 절대 금지입니다. 식용 버섯도 깨끗이 씻어 익혀서 소량만 주세요.');
/*!40000 ALTER TABLE `food` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `login_id` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UQ_email` (`email`),
  UNIQUE KEY `UQ_login_id` (`login_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pet`
--

DROP TABLE IF EXISTS `pet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pet` (
  `pet_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `breed_id` int NOT NULL,
  `petname` varchar(50) NOT NULL,
  `birthdate` date NOT NULL,
  `gender` char(1) NOT NULL,
  `neuter` tinyint(1) NOT NULL,
  `weight` decimal(5,2) NOT NULL,
  PRIMARY KEY (`pet_id`),
  KEY `FK_Member_TO_Pet` (`user_id`),
  KEY `FK_Breed_TO_Pet` (`breed_id`),
  CONSTRAINT `FK_Breed_TO_Pet` FOREIGN KEY (`breed_id`) REFERENCES `breed` (`breed_id`),
  CONSTRAINT `FK_Member_TO_Pet` FOREIGN KEY (`user_id`) REFERENCES `member` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pet`
--

LOCK TABLES `pet` WRITE;
/*!40000 ALTER TABLE `pet` DISABLE KEYS */;
/*!40000 ALTER TABLE `pet` ENABLE KEYS */;
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

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-12 14:05:27
