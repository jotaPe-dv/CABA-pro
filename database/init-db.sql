-- ============================================
-- CABA Pro - Script de Inicialización de BD
-- ============================================
-- Este script crea las tablas y datos iniciales
-- para el sistema CABA Pro

-- Usar la base de datos
USE caba_pro;

-- ============================================
-- ELIMINAR TABLAS SI EXISTEN (SOLO DESARROLLO)
-- ============================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS liquidacion;
DROP TABLE IF EXISTS asignacion;
DROP TABLE IF EXISTS partido;
DROP TABLE IF EXISTS tarifa;
DROP TABLE IF EXISTS torneo;
DROP TABLE IF EXISTS arbitro;
DROP TABLE IF EXISTS administrador;
DROP TABLE IF EXISTS usuario;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- CREAR TABLAS
-- ============================================

-- Tabla Usuario (clase base)
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_rol (rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Arbitro
CREATE TABLE arbitro (
    id BIGINT PRIMARY KEY,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    foto_url VARCHAR(500),
    escalafon VARCHAR(50) NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    total_partidos INT DEFAULT 0,
    total_ingresos DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE,
    INDEX idx_escalafon (escalafon),
    INDEX idx_disponible (disponible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Administrador
CREATE TABLE administrador (
    id BIGINT PRIMARY KEY,
    cargo VARCHAR(100),
    nivel_acceso VARCHAR(50),
    FOREIGN KEY (id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Torneo
CREATE TABLE torneo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fechas (fecha_inicio, fecha_fin),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Tarifa
CREATE TABLE tarifa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    torneo_id BIGINT NOT NULL,
    escalafon VARCHAR(50) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    vigente BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (torneo_id) REFERENCES torneo(id) ON DELETE CASCADE,
    INDEX idx_torneo_escalafon (torneo_id, escalafon)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Partido
CREATE TABLE partido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    torneo_id BIGINT NOT NULL,
    equipo_local VARCHAR(100) NOT NULL,
    equipo_visitante VARCHAR(100) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    ubicacion VARCHAR(200) NOT NULL,
    estado VARCHAR(50) DEFAULT 'PROGRAMADO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (torneo_id) REFERENCES torneo(id) ON DELETE CASCADE,
    INDEX idx_fecha (fecha_hora),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Asignacion
CREATE TABLE asignacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    arbitro_id BIGINT NOT NULL,
    partido_id BIGINT NOT NULL,
    estado VARCHAR(50) DEFAULT 'PENDIENTE',
    comentario TEXT,
    monto DECIMAL(10,2) NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta TIMESTAMP NULL,
    FOREIGN KEY (arbitro_id) REFERENCES arbitro(id) ON DELETE CASCADE,
    FOREIGN KEY (partido_id) REFERENCES partido(id) ON DELETE CASCADE,
    INDEX idx_estado (estado),
    INDEX idx_arbitro (arbitro_id),
    INDEX idx_partido (partido_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Liquidacion
CREATE TABLE liquidacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    arbitro_id BIGINT NOT NULL,
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(50) DEFAULT 'PENDIENTE',
    fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_pago TIMESTAMP NULL,
    observaciones TEXT,
    FOREIGN KEY (arbitro_id) REFERENCES arbitro(id) ON DELETE CASCADE,
    INDEX idx_arbitro_periodo (arbitro_id, periodo_inicio, periodo_fin),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- INSERTAR DATOS INICIALES
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

-- ============================================
-- VERIFICAR DATOS
-- ============================================

-- Ver usuarios creados
SELECT u.id, u.nombre, u.email, u.rol, a.escalafon, a.disponible 
FROM usuario u 
LEFT JOIN arbitro a ON u.id = a.id 
WHERE u.activo = TRUE;

-- Ver torneos
SELECT * FROM torneo WHERE activo = TRUE;

-- Ver tarifas
SELECT t.nombre AS torneo, ta.escalafon, ta.monto 
FROM tarifa ta 
JOIN torneo t ON ta.torneo_id = t.id 
WHERE ta.vigente = TRUE;

-- Ver partidos programados
SELECT p.id, t.nombre AS torneo, p.equipo_local, p.equipo_visitante, 
       p.fecha_hora, p.ubicacion, p.estado
FROM partido p
JOIN torneo t ON p.torneo_id = t.id
ORDER BY p.fecha_hora;

-- Ver asignaciones pendientes
SELECT a.id, u.nombre AS arbitro, ar.escalafon, 
       p.equipo_local, p.equipo_visitante, p.fecha_hora,
       a.monto, a.estado
FROM asignacion a
JOIN arbitro ar ON a.arbitro_id = ar.id
JOIN usuario u ON ar.id = u.id
JOIN partido p ON a.partido_id = p.id
WHERE a.estado = 'PENDIENTE';

-- ============================================
-- FIN DEL SCRIPT
-- ============================================

SELECT '✅ Base de datos inicializada correctamente!' AS Status;
