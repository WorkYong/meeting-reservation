-- =========================
-- FULL INIT + SEED (MySQL8)
-- =========================

SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS wiseaimeetingreservation
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE wiseaimeetingreservation;

-- DROP in correct order
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS payment_providers;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS meeting_rooms;
DROP TABLE IF EXISTS users;

-- USERS (name -> username)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL
) ENGINE=InnoDB;

-- MEETING ROOMS
CREATE TABLE IF NOT EXISTS meeting_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    capacity INT NOT NULL,
    hourly_price INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL
) ENGINE=InnoDB;

-- RESERVATIONS
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    meeting_room_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status ENUM('PENDING_PAYMENT','PAID','CONFIRMED','CANCELLED') NOT NULL DEFAULT 'PENDING_PAYMENT',
    total_price INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    UNIQUE KEY uq_reservation_exact (meeting_room_id, start_time, end_time),
    INDEX idx_reservation_room_time (meeting_room_id, start_time, end_time),
    INDEX idx_reservation_user (user_id),
    INDEX idx_reservation_status (status)
) ENGINE=InnoDB;

-- PAYMENT PROVIDERS
CREATE TABLE IF NOT EXISTS payment_providers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    api_endpoint VARCHAR(255) NOT NULL,
    auth_key TEXT NULL,
    provider_type ENUM('CARD','SIMPLE','VIRTUAL_ACCOUNT') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL
) ENGINE=InnoDB;

-- PAYMENTS
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL UNIQUE,
    provider_type ENUM('CARD','SIMPLE','VIRTUAL_ACCOUNT') NOT NULL,
    amount INT NOT NULL,
    status ENUM('PENDING','SUCCESS','FAILED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    external_payment_id VARCHAR(100) UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    INDEX idx_payment_status (status)
) ENGINE=InnoDB;

-- FOREIGN KEYS
ALTER TABLE reservations
  ADD CONSTRAINT fk_res_user  FOREIGN KEY (user_id) REFERENCES users(id),
  ADD CONSTRAINT fk_res_room  FOREIGN KEY (meeting_room_id) REFERENCES meeting_rooms(id);

ALTER TABLE payments
  ADD CONSTRAINT fk_pay_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id);

SET FOREIGN_KEY_CHECKS = 1;

-- ==============
-- SEEDING DATA
-- ==============
START TRANSACTION;

-- USERS (10) - Avengers 스타일
INSERT INTO users (username, email, password) VALUES
('Tony Stark',     'tony.stark@example.com',    '$2a$10$hashTony'),
('Steve Rogers',   'steve.rogers@example.com',  '$2a$10$hashSteve'),
('Bruce Banner',   'bruce.banner@example.com',  '$2a$10$hashBruce'),
('Natasha Romanoff','natasha.romanoff@example.com','$2a$10$hashNatasha'),
('Clint Barton',   'clint.barton@example.com',  '$2a$10$hashClint'),
('Thor Odinson',   'thor.odinson@example.com',  '$2a$10$hashThor'),
('Wanda Maximoff', 'wanda.maximoff@example.com','$2a$10$hashWanda'),
('Vision',         'vision@example.com',        '$2a$10$hashVision'),
('Peter Parker',   'peter.parker@example.com',  '$2a$10$hashPeter'),
('Stephen Strange','stephen.strange@example.com','$2a$10$hashStephen');

-- MEETING ROOMS (4)
INSERT INTO meeting_rooms (name, capacity, hourly_price) VALUES
('Alpha Room',   4, 10000),
('Beta Room',    8, 15000),
('Gamma Room',  12, 20000),
('Delta Room',  20, 30000);

-- PAYMENT PROVIDERS (3)
INSERT INTO payment_providers (name, api_endpoint, auth_key, provider_type) VALUES
('MockCard',   'http://mock-pay.local/a/card',   NULL, 'CARD'),
('MockSimple', 'http://mock-pay.local/a/simple', NULL, 'SIMPLE'),
('MockVA',     'http://mock-pay.local/a/va',     NULL, 'VIRTUAL_ACCOUNT');

-- RESERVATIONS (10)
INSERT INTO reservations (user_id, meeting_room_id, start_time, end_time, status, total_price) VALUES
(1, 1, '2025-08-15 09:00:00', '2025-08-15 10:30:00', 'CONFIRMED',        15000),
(2, 2, '2025-08-15 11:00:00', '2025-08-15 13:00:00', 'PAID',             30000),
(3, 3, '2025-08-15 14:00:00', '2025-08-15 15:00:00', 'PENDING_PAYMENT',  20000),
(4, 4, '2025-08-16 09:00:00', '2025-08-16 12:00:00', 'CONFIRMED',        90000),
(5, 1, '2025-08-16 13:00:00', '2025-08-16 14:00:00', 'CANCELLED',        10000),
(6, 2, '2025-08-17 09:30:00', '2025-08-17 10:00:00', 'PAID',              7500),
(7, 3, '2025-08-17 10:00:00', '2025-08-17 12:30:00', 'CONFIRMED',        50000),
(8, 4, '2025-08-18 15:00:00', '2025-08-18 16:00:00', 'PENDING_PAYMENT',  30000),
(9, 1, '2025-08-19 09:00:00', '2025-08-19 11:00:00', 'PAID',             20000),
(10,2, '2025-08-20 14:00:00', '2025-08-20 15:30:00', 'CONFIRMED',        22500);

-- PAYMENTS (8)
INSERT INTO payments (reservation_id, provider_type, amount, status, external_payment_id) VALUES
(1,  'CARD',            15000, 'SUCCESS',   'CARD-A001'),
(2,  'SIMPLE',          30000, 'SUCCESS',   'SIMPLE-B001'),
(3,  'CARD',            20000, 'PENDING',   'CARD-A002'),
(4,  'VIRTUAL_ACCOUNT', 90000, 'SUCCESS',   'VA-C001'),
(5,  'CARD',            10000, 'CANCELLED', 'CARD-A003'),
(6,  'SIMPLE',           7500, 'SUCCESS',   'SIMPLE-B002'),
(7,  'CARD',            50000, 'SUCCESS',   'CARD-A004'),
(9,  'CARD',            20000, 'SUCCESS',   'CARD-A005');

COMMIT;

-- quick sanity checks
SELECT COUNT(*) AS users_cnt        FROM users;
SELECT COUNT(*) AS rooms_cnt        FROM meeting_rooms;
SELECT COUNT(*) AS reservations_cnt FROM reservations;
SELECT COUNT(*) AS payments_cnt     FROM payments;
