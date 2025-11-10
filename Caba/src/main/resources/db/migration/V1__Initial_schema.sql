-- ============================================
-- CABA Pro - Initial Database Schema
-- Version: 1.0.0
-- Flyway Migration: V1__Initial_schema.sql
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
