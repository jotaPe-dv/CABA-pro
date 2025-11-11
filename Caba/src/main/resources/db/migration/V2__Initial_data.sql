-- ============================================
-- CABA Pro - Initial Data
-- Version: 2.2 - Datos de torneos y tarifas
-- Flyway Migration: V2__Initial_data.sql
-- ============================================
-- NOTA: Los usuarios (admin y árbitros) se crean automáticamente
--       en DataLoader.java con BCrypt correcto
-- Password para login: "123456" para todos los usuarios
-- ============================================

-- ============================================
-- USUARIOS: Creados automáticamente por DataLoader.java
-- ============================================
-- Admin: admin@caba.com / 123456
-- Árbitro Principal: principal@caba.com / 123456
-- Árbitro Asistente: asistente@caba.com / 123456
-- Árbitro Mesa: mesa@caba.com / 123456
-- ============================================

-- ============================================
-- TORNEOS
-- ============================================
-- NOTA: administrador_id será NULL, se asigna después en la aplicación
INSERT INTO torneos (nombre, descripcion, fecha_inicio, fecha_fin, activo, administrador_id, fecha_creacion, cerrado) VALUES
('Liga Profesional 2025', 'Temporada de baloncesto profesional Colombia', '2025-01-15', '2025-06-30', TRUE, NULL, NOW(), FALSE);

-- ============================================
-- TARIFAS (12 combinaciones)
-- ============================================
INSERT INTO tarifas (escalafon, tipo_partido, monto, descripcion, fecha_creacion, fecha_vigencia_inicio, activa) VALUES
-- Finales
('PRINCIPAL', 'Final', 200000.00, 'Árbitro principal - Final', NOW(), NOW(), TRUE),
('AUXILIAR_1', 'Final', 150000.00, 'Árbitro auxiliar 1 - Final', NOW(), NOW(), TRUE),
('AUXILIAR_2', 'Final', 120000.00, 'Árbitro auxiliar 2 - Final', NOW(), NOW(), TRUE),
-- Semifinales
('PRINCIPAL', 'Semifinal', 170000.00, 'Árbitro principal - Semifinal', NOW(), NOW(), TRUE),
('AUXILIAR_1', 'Semifinal', 130000.00, 'Árbitro auxiliar 1 - Semifinal', NOW(), NOW(), TRUE),
('AUXILIAR_2', 'Semifinal', 100000.00, 'Árbitro auxiliar 2 - Semifinal', NOW(), NOW(), TRUE),
-- Cuartos
('PRINCIPAL', 'Cuartos', 150000.00, 'Árbitro principal - Cuartos', NOW(), NOW(), TRUE),
('AUXILIAR_1', 'Cuartos', 110000.00, 'Árbitro auxiliar 1 - Cuartos', NOW(), NOW(), TRUE),
('AUXILIAR_2', 'Cuartos', 90000.00, 'Árbitro auxiliar 2 - Cuartos', NOW(), NOW(), TRUE),
-- Regular
('PRINCIPAL', 'Regular', 130000.00, 'Árbitro principal - Regular', NOW(), NOW(), TRUE),
('AUXILIAR_1', 'Regular', 100000.00, 'Árbitro auxiliar 1 - Regular', NOW(), NOW(), TRUE),
('AUXILIAR_2', 'Regular', 80000.00, 'Árbitro auxiliar 2 - Regular', NOW(), NOW(), TRUE);

-- ============================================
-- PARTIDOS
-- ============================================
INSERT INTO partidos (torneo_id, fecha_partido, equipo_local, equipo_visitante, tipo_partido, ubicacion, completado, fecha_creacion, observaciones) VALUES
(1, '2025-11-15 19:00:00', 'Titanes Barranquilla', 'Búcaros Bucaramanga', 'Regular', 'Coliseo Elías Chegwin', FALSE, NOW(), 'Jornada 1'),
(1, '2025-11-16 20:00:00', 'Piratas Bogotá', 'Team Cali', 'Regular', 'Coliseo El Salitre', FALSE, NOW(), 'Jornada 2'),
(1, '2025-11-17 18:30:00', 'Cafeteros Armenia', 'Motilones Norte', 'Regular', 'Coliseo del Café', FALSE, NOW(), 'Jornada 3');

-- ============================================
-- ASIGNACIONES
-- ============================================
-- Partido 1: Asignación completa (Principal + Asistente + Mesa)
INSERT INTO asignaciones (partido_id, arbitro_id, tarifa_id, rol_especifico, estado, fecha_asignacion, monto_calculado, comentarios) VALUES
(1, 2, 10, 'PRINCIPAL', 'ACEPTADA', NOW(), 130000.00, 'Confirmado'),
(1, 3, 11, 'AUXILIAR_1', 'ACEPTADA', NOW(), 100000.00, 'Confirmado'),
(1, 4, 12, 'AUXILIAR_2', 'PENDIENTE', NOW(), 80000.00, 'Pendiente confirmación');

-- ============================================
-- CONFIGURACIONES
-- ============================================
INSERT INTO configuraciones (clave, valor, descripcion, modificado_por) VALUES
('VALIDAR_ARBITROS_SIMULACION', 'true', 'Validar asignaciones antes de simular', 'SYSTEM'),
('DIAS_ANTICIPO_ASIGNACION', '7', 'Días mínimos de anticipación', 'SYSTEM'),
('MAX_PARTIDOS_POR_ARBITRO_DIA', '2', 'Máximo de partidos por árbitro/día', 'SYSTEM'),
('HABILITAR_NOTIFICACIONES_EMAIL', 'true', 'Enviar notificaciones por email', 'SYSTEM'),
('MONEDA', 'COP', 'Moneda del sistema', 'SYSTEM');

