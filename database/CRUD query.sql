-- MEMBER --
-- 비밀번호, 이메일 변경
UPDATE member
SET
	password = '변경 비밀번호',
    email = '변경 이메일'
WHERE user_id = 변경될 사용자 id값;

-- 계정 삭제
DELETE FROM member WHERE user_id = 삭제될 사용자 id값;


-- PET --
-- 반려동물 정보 조회
SELECT petname, birthdate, breed FROM pet WHERE pet_id = 조회 될 반려동물 id값;

-- 반려동물 정보 등록
INSERT INTO pet (user_id, petname, breed, birthdate, gender, neuter, weight)
VALUES (user_id 값, '반려동물 이름', '품종명', 'yyyy-mm-dd', 'M', false, 몸무게값);

-- 반려동물 정보 수정
UPDATE pet
SET
	petname = '수정된 반려동물 이름',
    breed = '수정된 반려동물 품종명',
	birthdate = 'yyyy-mm-dd',
    gender = 'F',
    neuter = true,
    weight = 몸무게 값
WHERE
pet_id = 수정할 반려동물 id;

-- 반려동물 정보 삭제
DELETE FROM pet WHERE pet_id = 삭제할 반려동물 id;

-- FOOD --
-- 검색어 조회
SELECT food_id, foodname FROM food
WHERE foodname LIKE CONCAT('%', '검색어', '%');

-- 상세정보 조회
SELECT foodname, edible, description, cautions FROM food
WHERE food_id = 조회할 음식 id;

-- SYMPTOM --
-- 증상 목록 조회
SELECT symptom_id, title, symptomdate FROM symptom
WHERE pet_id = 조회할 반려동물 id
ORDER BY symptomdate DESC, symptom_id DESC;

-- 증상 추가
INSERT INTO symptom (pet_id, title, symptom, symptomdate)
VALUES (반려동물 id, '구토 증상', '어제 사료를 먹고 구토를 함', '2025-11-12');

-- 증상 수정
UPDATE symptom
SET title = '구토 증상(수정)',
	symptom = '어제 사료를 먹고 구토를 함(수정)',
	symptomdate = '2025-11-13'
WHERE symptom_id = 수정할 증상 id;

-- 증상 삭제
DELETE FROM symptom
WHERE symptom_id = 삭제할 증상 id;