-- Insertar datos de usuario base (administrador)
INSERT INTO usuarios (nombre, email, password, telefono, fecha_registro) VALUES
('Administrador CABA', 'admin@caba.com', 'password123', '555-0001', '2024-01-01 10:00:00'),
('Juan Pérez', 'arbitro1@caba.com', 'password123', '555-0002', '2024-01-01 10:00:00'),
('María García', 'arbitro2@caba.com', 'password123', '555-0003', '2024-01-01 10:00:00'),
('Carlos López', 'arbitro3@caba.com', 'password123', '555-0004', '2024-01-01 10:00:00');

-- Insertar administrador
INSERT INTO administradores (usuario_id, nivel_acceso, departamento, activo) VALUES
(1, 'SUPER_ADMIN', 'Administración General', true);

-- Insertar árbitros
INSERT INTO arbitros (usuario_id, especialidad, escalafon) VALUES
(2, 'Principal', 'Nacional'),
(3, 'Principal', 'Provincial'),
(4, 'Asistente', 'Regional');

-- Insertar algunos partidos de ejemplo
INSERT INTO partido (equipo_local, equipo_visitante, fecha_hora, lugar) VALUES
('Equipo Alpha', 'Equipo Beta', '2024-12-15 15:00:00', 'Gimnasio Central'),
('Equipo Gamma', 'Equipo Delta', '2024-12-15 17:30:00', 'Polideportivo Norte'),
('Equipo Epsilon', 'Equipo Zeta', '2024-12-16 14:00:00', 'Club Deportivo'),
('Equipo Theta', 'Equipo Kappa', '2024-12-16 16:30:00', 'Centro Deportivo Sur');

-- Insertar algunas asignaciones de ejemplo
INSERT INTO asignacion (arbitro_id, partido_id, rol_especifico, pago_calculado, estado) VALUES
(2, 1, 'Árbitro Principal', 150.00, 'PENDIENTE'),
(3, 1, 'Árbitro Asistente', 100.00, 'PENDIENTE'),
(2, 2, 'Árbitro Principal', 150.00, 'ACEPTADA'),
(4, 2, 'Árbitro Asistente', 100.00, 'ACEPTADA'),
(3, 3, 'Árbitro Principal', 150.00, 'PENDIENTE'),
(4, 4, 'Árbitro Asistente', 100.00, 'RECHAZADA');
