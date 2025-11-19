-- Room Info Service Database Schema
-- PostgreSQL DDL Script

-- 1. Keyword 테이블 (독립 테이블)
CREATE TABLE keyword
(
    keyword_id BIGSERIAL PRIMARY KEY,
    keyword VARCHAR(50) NOT NULL UNIQUE
);

-- 2. RoomInfo 테이블 (메인 테이블)
CREATE TABLE room_info
(
    room_id BIGSERIAL PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL,
    place_id  BIGINT       NOT NULL,
    status    VARCHAR(20)  NOT NULL CHECK (status IN ('OPEN', 'CLOSE', 'PENDING')),
    time_slot VARCHAR(20) CHECK (time_slot IN ('HOUR', 'HALFHOUR'))
);

-- 3. StringAttribute 테이블 (단일 테이블 상속 전략)
-- FurtherDetail, CautionDetail을 포함
CREATE TABLE string_attribute
(
    attribute_id BIGSERIAL PRIMARY KEY,
    attribute_type VARCHAR(31)   NOT NULL CHECK (attribute_type IN ('FURTHER', 'CAUTION')),
    room_id        BIGINT        NOT NULL,
    contents       VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_string_attribute_room FOREIGN KEY (room_id) REFERENCES room_info (room_id) ON DELETE CASCADE
);

-- 4. RoomImage 테이블
CREATE TABLE room_image
(
    image_id  VARCHAR(100) PRIMARY KEY,
    room_id   BIGINT       NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    CONSTRAINT fk_room_image_room FOREIGN KEY (room_id) REFERENCES room_info (room_id) ON DELETE CASCADE
);

-- 5. RoomOptionsMapper 테이블 (다대다 매핑 테이블)
CREATE TABLE room_options_mapper
(
    room_id    BIGINT NOT NULL,
    keyword_id BIGINT NOT NULL,
    PRIMARY KEY (room_id, keyword_id),
    CONSTRAINT fk_room_options_room FOREIGN KEY (room_id) REFERENCES room_info (room_id) ON DELETE CASCADE,
    CONSTRAINT fk_room_options_keyword FOREIGN KEY (keyword_id) REFERENCES keyword (keyword_id) ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX idx_room_info_place_id ON room_info (place_id);
CREATE INDEX idx_room_info_status ON room_info (status);
CREATE INDEX idx_string_attribute_room_id ON string_attribute (room_id);
CREATE INDEX idx_string_attribute_type ON string_attribute (attribute_type);
CREATE INDEX idx_room_image_room_id ON room_image (room_id);
CREATE INDEX idx_room_options_keyword_id ON room_options_mapper (keyword_id);

-- 시퀀스 확인 (자동 생성되지만 명시적으로 확인 가능)
-- SELECT * FROM pg_sequences WHERE schemaname = 'public';
