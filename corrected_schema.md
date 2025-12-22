```sql
-- 데이터베이스 선택
USE springcommunity_db;

-- 기존 테이블 삭제 (의존성 역순으로)
DROP TABLE IF EXISTS chat_message;
DROP TABLE IF EXISTS chat_room;
DROP TABLE IF EXISTS product_wishlist;
DROP TABLE IF EXISTS product_image;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS user;

-- user 테이블 생성 (수정됨)
-- provider, providerId 추가 / password는 NULL 허용
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    password VARCHAR(255),
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    provider VARCHAR(50),
    providerId VARCHAR(255)
);

-- product 테이블 생성
CREATE TABLE product (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(255) NOT NULL,
    price INT COMMENT '가격',
    wishlist_count INT COMMENT '찜하기 수',
    views INT COMMENT '조회수',
    deal_region VARCHAR(255) COMMENT '거래 희망지역',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_user FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- product_image 테이블 생성
CREATE TABLE product_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '이미지 ID (PK)',
    product_id BIGINT NOT NULL COMMENT '게시글 ID (FK)',
    original_file_name VARCHAR(255) NOT NULL COMMENT '원본 파일명',
    stored_file_name VARCHAR(255) NOT NULL COMMENT '서버에 저장될 파일명',
    file_path VARCHAR(500) NOT NULL COMMENT '서버에 저장된 파일 경로',
    file_size BIGINT NOT NULL COMMENT '파일 크기(Byte)',
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    CONSTRAINT fk_product_image FOREIGN KEY (product_id) REFERENCES product(id)
        ON DELETE CASCADE
);

-- product_wishlist 테이블 생성
CREATE TABLE product_wishlist(
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- chat_room 테이블 생성
CREATE TABLE chat_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chatroom_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_chatroom_buyer FOREIGN KEY (buyer_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_chatroom_seller FOREIGN KEY (seller_id) REFERENCES user(id) ON DELETE CASCADE
);

-- chat_message 테이블 생성 (수정됨)
-- sender -> sender_id 로 변경 및 user 테이블 외래키 추가
CREATE TABLE chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chatmessage_room FOREIGN KEY (room_id) REFERENCES chat_room(id) ON DELETE CASCADE,
    CONSTRAINT fk_chatmessage_sender FOREIGN KEY (sender_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 생성된 테이블 확인
SELECT * FROM user;
SELECT * FROM product;
SELECT * FROM product_image;
SELECT * FROM product_wishlist;
SELECT * FROM chat_room;
SELECT * FROM chat_message;
```
