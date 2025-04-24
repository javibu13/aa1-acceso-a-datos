-- Insertar datos en la tabla 'person'
INSERT INTO person (first_name, last_name, email, pass, age, height, interests, birth_date, verified) VALUES
('Juan', 'Perez', 'juan.perez@example.com', 'password123', 30, 1.75, 'Programacion, deportes', '1994-05-15', TRUE),
('Maria', 'Gomez', 'maria.gomez@example.com', 'securePass', 25, 1.68, 'Lectura, musica', '1999-10-20', FALSE);

-- Insertar datos en la tabla 'place'
INSERT INTO place (name, address, capacity, area, inauguration_date, has_parking, equipment) VALUES
('Salon Principal', 'Calle Falsa 123', 200, 150.50, '2020-01-01', TRUE, 'Proyectores, sonido'),
('Auditorio Norte', 'Avenida Siempreviva 456', 100, 80.00, '2018-06-01', FALSE, 'Pantalla, microfonos');

-- Insertar datos en la tabla 'conference'
INSERT INTO conference (name, capacity, budget, online, start_date, place_id, organizer_id) VALUES
('Conferencia de Tecnologia', 150, 10000.00, FALSE, '2024-05-01', 1, 1),
('Webinar de Marketing', 50, 2000.00, TRUE, '2024-06-15', 2, 2);

-- Insertar datos en la tabla 'activity'
INSERT INTO activity (title, duration, price, open, schedule, conference_id) VALUES
('Charla sobre IA', 60, 50.00, TRUE, '2024-05-01 10:00:00', 1),
('Taller de SEO', 90, 25.00, FALSE, '2024-06-15 14:00:00', 2);

-- Insertar datos en la tabla 'attendance'
INSERT INTO attendance (ticket_code, seat_number, ticket_price, checked_in, person_id, conference_id) VALUES
('TK123', 15, 75.00, TRUE, 1, 1),
('WB456', NULL, 15.00, FALSE, 2, 2);