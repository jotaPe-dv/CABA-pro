-- ============================================
-- CABA Pro - Initial Data
-- Version: 1.0.1
-- Flyway Migration: V2__Initial_data.sql
-- ============================================

-- Insertar Usuarios y Árbitros de prueba
-- Password: 123456 (bcrypt hash)
INSERT INTO usuario (nombre, email, password, rol, activo) VALUES
('Juan Pérez', 'principal@caba.com', '$2a$10$8Z9K5P6rQ7s8T9uV0wX1yO9Z8K5P6rQ7s8T9uV0wX1yO9Z8K5P6rQ', 'ARBITRO', TRUE),
('María González', 'auxiliar1@caba.com', '$2a$10$8Z9K5P6rQ7s8T9uV0wX1yO9Z8K5P6rQ7s8T9uV0wX1yO9Z8K5P6rQ', 'ARBITRO', TRUE),
('Carlos Rodríguez', 'auxiliar2@caba.com', '$2a$10$8Z9K5P6rQ7s8T9uV0wX1yO9Z8K5P6rQ7s8T9uV0wX1yO9Z8K5P6rQ', 'ARBITRO', TRUE),
('Admin Principal', 'admin@caba.com', '$2a$10$8Z9K5P6rQ7s8T9uV0wX1yO9Z8K5P6rQ7s8T9uV0wX1yO9Z8K5P6rQ', 'ADMINISTRADOR', TRUE);

INSERT INTO arbitro (id, telefono, direccion, escalafon, disponible, total_partidos, total_ingresos) VALUES
(1, '3001234567', 'Calle 123 #45-67, Bogotá', 'PRINCIPAL', TRUE, 0, 0.00),
(2, '3009876543', 'Carrera 45 #12-34, Medellín', 'AUXILIAR_1', TRUE, 0, 0.00),
(3, '3005556789', 'Avenida 68 #23-45, Cali', 'AUXILIAR_2', TRUE, 0, 0.00);

INSERT INTO administrador (id, cargo, nivel_acceso) VALUES
(4, 'Administrador General', 'TOTAL');

-- Insertar Torneo de prueba
INSERT INTO torneo (nombre, descripcion, fecha_inicio, fecha_fin, activo) VALUES
('Liga Nacional 2025', 'Torneo de baloncesto profesional Colombia', '2025-01-15', '2025-12-15', TRUE);

-- Insertar Tarifas
INSERT INTO tarifa (torneo_id, escalafon, monto, vigente) VALUES
(1, 'PRINCIPAL', 150000.00, TRUE),
(1, 'AUXILIAR_1', 120000.00, TRUE),
(1, 'AUXILIAR_2', 100000.00, TRUE),
(1, 'COMISIONADO', 200000.00, TRUE);

-- Insertar Partidos de prueba
INSERT INTO partido (torneo_id, equipo_local, equipo_visitante, fecha_hora, ubicacion, estado) VALUES
(1, 'Bulls Chicago', 'Lakers Los Angeles', '2025-11-15 19:00:00', 'Arena Bogotá', 'PROGRAMADO'),
(1, 'Heat Miami', 'Warriors Golden State', '2025-11-16 20:00:00', 'Coliseo Medellín', 'PROGRAMADO'),
(1, 'Celtics Boston', 'Nets Brooklyn', '2025-11-17 18:30:00', 'Estadio Cali', 'PROGRAMADO');

-- Insertar Asignaciones de prueba
INSERT INTO asignacion (arbitro_id, partido_id, estado, monto, comentario) VALUES
(1, 1, 'PENDIENTE', 150000.00, 'Asignación automática'),
(2, 1, 'PENDIENTE', 120000.00, 'Asignación automática'),
(3, 1, 'PENDIENTE', 100000.00, 'Asignación automática');
