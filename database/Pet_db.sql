
CREATE TABLE Breed
(
  breed_id  NUMBER(10)   NOT NULL,
  breedname VARCHAR2(50) NOT NULL,
  CONSTRAINT PK_Breed PRIMARY KEY (breed_id)
);

ALTER TABLE Breed
  ADD CONSTRAINT UQ_breedname UNIQUE (breedname);

CREATE SEQUENCE SEQ_Breed
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER SEQ_TRG_Breed
BEFORE INSERT ON Breed
REFERENCING NEW AS NEW FOR EACH ROW
BEGIN
  SELECT SEQ_Breed.NEXTVAL
  INTO: NEW.breed_id
  FROM DUAL;
END;

COMMENT ON TABLE Breed IS '품종 정보';

COMMENT ON COLUMN Breed.breed_id IS '품종 고유 식별';

COMMENT ON COLUMN Breed.breedname IS '품종 이름';

CREATE TABLE Food
(
  food_id     NUMBER(10)    NOT NULL,
  foodname    VARCHAR2(100) NOT NULL,
  edible      NUMBER(1)     NOT NULL,
  description CLOB         ,
  cautions    CLOB         ,
  CONSTRAINT PK_Food PRIMARY KEY (food_id)
);

ALTER TABLE Food
  ADD CONSTRAINT UQ_foodname UNIQUE (foodname);

CREATE SEQUENCE SEQ_Food
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER SEQ_TRG_Food
BEFORE INSERT ON Food
REFERENCING NEW AS NEW FOR EACH ROW
BEGIN
  SELECT SEQ_Food.NEXTVAL
  INTO: NEW.food_id
  FROM DUAL;
END;

COMMENT ON TABLE Food IS '음식 정보';

COMMENT ON COLUMN Food.food_id IS '음식 고유 식별';

COMMENT ON COLUMN Food.foodname IS '음식 이름';

COMMENT ON COLUMN Food.edible IS '섭치 가능 여부(0,1)';

COMMENT ON COLUMN Food.description IS '설명(장점,단점)';

COMMENT ON COLUMN Food.cautions IS '주의 사항';

CREATE TABLE Pet
(
  pet_id    NUMBER(10)   NOT NULL,
  user_id   NUMBER(10)   NOT NULL,
  breed_id  NUMBER(10)   NOT NULL,
  petname   VARCAR2(50)  NOT NULL,
  birthdate DATE         NOT NULL,
  gender    CHAR(1)      NOT NULL,
  neuter    NUMBER(1)    NOT NULL,
  weight    NUMBER(5, 2),
  CONSTRAINT PK_Pet PRIMARY KEY (pet_id)
);

CREATE SEQUENCE SEQ_Pet
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER SEQ_TRG_Pet
BEFORE INSERT ON Pet
REFERENCING NEW AS NEW FOR EACH ROW
BEGIN
  SELECT SEQ_Pet.NEXTVAL
  INTO: NEW.pet_id
  FROM DUAL;
END;

COMMENT ON TABLE Pet IS '반려동물 정보';

COMMENT ON COLUMN Pet.pet_id IS '반려동물 고유 식별';

COMMENT ON COLUMN Pet.user_id IS '회원 고유 식별';

COMMENT ON COLUMN Pet.breed_id IS '품종 고유 식별';

COMMENT ON COLUMN Pet.petname IS '반려동물 이름';

COMMENT ON COLUMN Pet.birthdate IS '반려동물 생년월일';

COMMENT ON COLUMN Pet.gender IS '반려동물 성별(F,M)';

COMMENT ON COLUMN Pet.neuter IS '중성화 여부';

COMMENT ON COLUMN Pet.weight IS '반려동물 체중';

CREATE TABLE Symptom
(
  symptom_id  NUMBER(10) NOT NULL,
  pet_id      NUMBER(10) NOT NULL,
  symptom     CLOB       NOT NULL,
  symptomdate DATE       NOT NULL,
  CONSTRAINT PK_Symptom PRIMARY KEY (symptom_id)
);

COMMENT ON TABLE Symptom IS '증상 기록';

COMMENT ON COLUMN Symptom.symptom_id IS '증상 고유 식별';

COMMENT ON COLUMN Symptom.pet_id IS '반려동물 고유 식별';

COMMENT ON COLUMN Symptom.symptom IS '증상';

COMMENT ON COLUMN Symptom.symptomdate IS '증상 기록 날짜';

CREATE TABLE User
(
  user_id  NUMBER(10)    NOT NULL,
  email    VARCHAR2(255) NOT NULL,
  login_id VARCHAR2(50)  NOT NULL,
  password VARCAR2(255)  NOT NULL,
  CONSTRAINT PK_User PRIMARY KEY (user_id)
);

ALTER TABLE User
  ADD CONSTRAINT UQ_email UNIQUE (email);

ALTER TABLE User
  ADD CONSTRAINT UQ_login_id UNIQUE (login_id);

CREATE SEQUENCE SEQ_User
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER SEQ_TRG_User
BEFORE INSERT ON User
REFERENCING NEW AS NEW FOR EACH ROW
BEGIN
  SELECT SEQ_User.NEXTVAL
  INTO: NEW.user_id
  FROM DUAL;
END;

COMMENT ON TABLE User IS '회원 정보';

COMMENT ON COLUMN User.user_id IS '회원 고유 식별';

COMMENT ON COLUMN User.email IS '이메일';

COMMENT ON COLUMN User.login_id IS '로그인 아이디';

COMMENT ON COLUMN User.password IS '비밀번호 해시값';

ALTER TABLE Pet
  ADD CONSTRAINT FK_User_TO_Pet
    FOREIGN KEY (user_id)
    REFERENCES User (user_id);

ALTER TABLE Pet
  ADD CONSTRAINT FK_Breed_TO_Pet
    FOREIGN KEY (breed_id)
    REFERENCES Breed (breed_id);

ALTER TABLE Symptom
  ADD CONSTRAINT FK_Pet_TO_Symptom
    FOREIGN KEY (pet_id)
    REFERENCES Pet (pet_id);
