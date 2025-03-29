CREATE TABLE person (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    pass VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    height FLOAT, -- 9.99 max
    interests TEXT,
    birth_date DATE,
    verified BOOLEAN DEFAULT FALSE
);

CREATE TABLE place (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    capacity INT,
    area FLOAT,
    inauguration_date DATE,
    has_parking BOOLEAN DEFAULT FALSE,
    equipment TEXT
);

CREATE TABLE conference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    capacity INT,
    budget FLOAT,
    online BOOLEAN DEFAULT FALSE,
    start_date DATE NOT NULL,
    place_id BIGINT NOT NULL,
    organizer_id BIGINT NOT NULL,

    FOREIGN KEY (place_id) REFERENCES place(id) ON DELETE CASCADE,
    FOREIGN KEY (organizer_id) REFERENCES person(id) ON DELETE CASCADE
);

CREATE TABLE activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration INT NOT NULL,
    price FLOAT,
    open BOOLEAN DEFAULT FALSE,
    schedule DATETIME NOT NULL,
    conference_id BIGINT NOT NULL,

    FOREIGN KEY (conference_id) REFERENCES conference(id) ON DELETE CASCADE
);

CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_code VARCHAR(255) NOT NULL,
    seat_number INT,
    ticketPrice FLOAT,
    checked_in BOOLEAN DEFAULT FALSE,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    person_id BIGINT NOT NULL,
    conference_id BIGINT NOT NULL,

    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE,
    FOREIGN KEY (conference_id) REFERENCES conference(id) ON DELETE CASCADE
);