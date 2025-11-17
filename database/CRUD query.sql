-- MEMBER --
-- 계정 삭제
DELETE FROM member WHERE user_id = 1;


-- PET --
-- 반려동물 정보 조회
SELECT petname, birthdate, breed FROM pet WHERE pet_id = 1;

-- 반려동물 정보 등록
INSERT INTO breed (breedname) VALUES ('말티즈');
INSERT INTO pet (user_id, petname, breed, birthdate, gender, neuter, weight)
VALUES (1, '티즈', 1, '2024-10-10', 'F', false, 2.12);

-- 반려동물 정보 수정
UPDATE pet
SET
	petname = '티즐',
    breed = '푸들',
	birthdate = '2024-10-11',
    gender = 'M',
    neuter = true,
    weight = 3.11
WHERE
pet_id = 1;

-- 반려동물 정보 삭제
DELETE FROM pet WHERE pet_id = 1;

-- FOOD --
-- 검색어 조회
SELECT food_id, foodname FROM food
WHERE foodname LIKE CONCAT('%', '감자', '%');

-- 상세정보 조회
SELECT foodname, edible, description, cautions FROM food
WHERE food_id = 1;

-- SYMPTOM --
-- 증상 목록 조회
SELECT symptom_id, title, symptomdate FROM symptom
WHERE pet_id = 1
ORDER BY symptomdate DESC, symptom_id DESC;

-- 증상 추가
INSERT INTO symptom (pet_id, title, symptom, symptomdate)
VALUES (1, '구토 증상', '어제 사료를 먹고 구토를 함', '2025-11-12');

-- 증상 수정
UPDATE symptom
SET title = '구토 증상(수정)',
	symptom = '어제 사료를 먹고 구토를 함(수정)',
	symptomdate = '2025-11-13'
WHERE symptom_id = 1;

-- 증상 삭제
DELETE FROM symptom
WHERE symptom_id = 1;