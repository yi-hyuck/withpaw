CREATE DATABASE petdb;

use petdb;

CREATE TABLE Member
(
  user_id  INT          NOT NULL AUTO_INCREMENT,
  email    VARCHAR(255) NOT NULL,
  login_id VARCHAR(50)  NOT NULL,
  password VARCHAR(255) NOT NULL,
  PRIMARY KEY (user_id)
);

ALTER TABLE Member
  ADD CONSTRAINT UQ_email UNIQUE (email);

ALTER TABLE Member
  ADD CONSTRAINT UQ_login_id UNIQUE (login_id);



CREATE TABLE Pet
(
  pet_id    INT          NOT NULL AUTO_INCREMENT,
  user_id   INT          NOT NULL,
  breed_id  INT          NOT NULL,
  petname   VARCHAR(50)  NOT NULL,
  birthdate DATE         NOT NULL,
  gender    CHAR(1)      NOT NULL,
  neuter    BOOLEAN      NOT NULL,
  weight    DECIMAL(5, 2) NOT NULL,
  PRIMARY KEY (pet_id)
);



CREATE TABLE Breed
(
  breed_id  INT          NOT NULL AUTO_INCREMENT,
  breedname VARCHAR(50)  NOT NULL,
  PRIMARY KEY (breed_id)
);

ALTER TABLE Breed
  ADD CONSTRAINT UQ_breedname UNIQUE (breedname);



CREATE TABLE Food
(
  food_id     INT           NOT NULL AUTO_INCREMENT,
  foodname    VARCHAR(100)  NOT NULL,
  edible      BOOLEAN       NOT NULL,
  description TEXT          NULL,
  cautions    TEXT          NULL,
  PRIMARY KEY (food_id)
);

ALTER TABLE Food
  ADD CONSTRAINT UQ_foodname UNIQUE (foodname);



CREATE TABLE Symptom
(
  symptom_id  INT           NOT NULL AUTO_INCREMENT,
  pet_id      INT           NOT NULL,
  title       VARCHAR(100)  NOT NULL,
  symptom     TEXT          NOT NULL,
  symptomdate DATE          NOT NULL,
  PRIMARY KEY (symptom_id)
);


ALTER TABLE Pet
  ADD CONSTRAINT FK_Member_TO_Pet
    FOREIGN KEY (user_id)
    REFERENCES Member (user_id);

ALTER TABLE Pet
  ADD CONSTRAINT FK_Breed_TO_Pet
    FOREIGN KEY (breed_id)
    REFERENCES Breed (breed_id);

ALTER TABLE Symptom
  ADD CONSTRAINT FK_Pet_TO_Symptom
    FOREIGN KEY (pet_id)
    REFERENCES Pet (pet_id);