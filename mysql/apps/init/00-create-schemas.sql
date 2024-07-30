-- สร้างฐานข้อมูล
CREATE DATABASE IF NOT EXISTS jwt_security;

-- ใช้ฐานข้อมูล coa
USE jwt_security;

-- สร้างผู้ใช้และให้สิทธิ์
CREATE USER IF NOT EXISTS 'jwt'@'%' IDENTIFIED BY 'jwt132';
GRANT ALL PRIVILEGES ON jwt.* TO 'jwt'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'jwt'@'%';
-- อัพเดทสิทธิ์
FLUSH PRIVILEGES;

