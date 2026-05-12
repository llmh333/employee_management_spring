CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    location VARCHAR(100),
    created_at DATETIME(6),
    updated_at DATETIME(6)
);
