-- Room ID: 249388389852393472에 이미지 추가 (역순으로 삽입)
-- 첫 번째 이미지 (sequence 1)
INSERT INTO room_image (image_id, room_id, image_url, sequence)
VALUES ('0f706420-2bb3-42bf-bb41-d2084883452b',
        249388389852393472,
        'http://teambind.co.kr:9200/images/POST/2025/11/30/0f706420-2bb3-42bf-bb41-d2084883452b.webp',
        1);

-- 두 번째 이미지 (sequence 2)
INSERT INTO room_image (image_id, room_id, image_url, sequence)
VALUES ('0035f8bb-d34e-4ba7-9ce4-442bc0ae3f39',
        249388389852393472,
        'https://teambind.co.kr:9200/images/PROFILE/2025/10/25/0035f8bb-d34e-4ba7-9ce4-442bc0ae3f39.webp',
        2);

-- 중복 체크를 위한 SELECT 문 (실행 전 확인용)
-- SELECT * FROM room_image WHERE room_id = 249388389852393472 ORDER BY sequence;

-- 기존 이미지가 있는 경우 삭제 후 삽입하려면 아래 명령 사용
-- DELETE FROM room_image WHERE room_id = 249388389852393472;
