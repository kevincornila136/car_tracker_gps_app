CREATE DATABASE gpsdb;
USE gpsdb;
CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id VARCHAR(50),
    latitude DOUBLE,
    longitude DOUBLE,
    speed FLOAT,
    timestamp BIGINT,
    session_id BIGINT
);


SELECT * FROM locations;