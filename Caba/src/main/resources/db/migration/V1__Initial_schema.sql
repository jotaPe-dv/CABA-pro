-- ============================================
-- CABA Pro - Initial Database Schema
-- Version: 1.0.0
-- Flyway Migration: V1__Initial_schema.sql
-- ============================================

-- Tabla Usuario (clase base)
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_usuario VARCHAR(50),
    INDEX idx_email (email),
    INDEX idx_tipo_usuario (tipo_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Arbitro
CREATE TABLE arbitros (
    id BIGINT PRIMARY KEY,
    numero_licencia VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    direccion VARCHAR(100),
    tarifa_base DECIMAL(10,2),
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    especialidad VARCHAR(30) NOT NULL,
    escalafon VARCHAR(30) NOT NULL,
    foto_url VARCHAR(255),
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_numero_licencia (numero_licencia),
    INDEX idx_escalafon (escalafon),
    INDEX idx_disponible (disponible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Administrador
CREATE TABLE administradores (
    id BIGINT PRIMARY KEY,
    cargo VARCHAR(50) NOT NULL,
    telefono VARCHAR(20),
    ultimo_acceso TIMESTAMP,
    permisos_especiales BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Torneo
CREATE TABLE torneos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    cerrado BOOLEAN DEFAULT FALSE,
    campeon VARCHAR(100),
    administrador_id BIGINT NOT NULL,
    FOREIGN KEY (administrador_id) REFERENCES administradores(id) ON DELETE RESTRICT,
    INDEX idx_fechas (fecha_inicio, fecha_fin),
    INDEX idx_activo (activo),
    INDEX idx_administrador (administrador_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Tarifa
CREATE TABLE tarifas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    escalafon VARCHAR(30) NOT NULL,
    tipo_partido VARCHAR(50) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(200),
    fecha_creacion DATETIME NOT NULL,
    fecha_vigencia_inicio DATETIME NOT NULL,
    fecha_vigencia_fin DATETIME NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE KEY uk_tipo_escalafon (tipo_partido, escalafon),
    INDEX idx_activa (activa),
    INDEX idx_tipo_partido (tipo_partido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Partido
CREATE TABLE partidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    torneo_id BIGINT NOT NULL,
    fecha_partido DATETIME NOT NULL,
    equipo_local VARCHAR(50) NOT NULL,
    equipo_visitante VARCHAR(50) NOT NULL,
    tipo_partido VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(100) NOT NULL,
    marcador_local INT,
    marcador_visitante INT,
    completado BOOLEAN NOT NULL DEFAULT FALSE,
    observaciones VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (torneo_id) REFERENCES torneos(id) ON DELETE CASCADE,
    INDEX idx_torneo_fecha (torneo_id, fecha_partido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Asignacion
CREATE TABLE asignaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    partido_id BIGINT NOT NULL,
    arbitro_id BIGINT NOT NULL,
    tarifa_id BIGINT NOT NULL,
    rol_especifico VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_asignacion DATETIME NOT NULL,
    fecha_respuesta DATETIME NULL,
    monto_calculado DECIMAL(10,2),
    comentarios VARCHAR(500),
    FOREIGN KEY (partido_id) REFERENCES partidos(id) ON DELETE CASCADE,
    FOREIGN KEY (arbitro_id) REFERENCES arbitros(id) ON DELETE CASCADE,
    FOREIGN KEY (tarifa_id) REFERENCES tarifas(id) ON DELETE RESTRICT,
    INDEX idx_estado (estado),
    INDEX idx_arbitro (arbitro_id),
    INDEX idx_partido (partido_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Liquidacion
CREATE TABLE liquidaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id BIGINT NOT NULL UNIQUE,
    monto DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion DATETIME NOT NULL,
    fecha_pago DATETIME NULL,
    observaciones VARCHAR(500),
    metodo_pago VARCHAR(100),
    referencia_pago VARCHAR(100),
    FOREIGN KEY (asignacion_id) REFERENCES asignaciones(id) ON DELETE CASCADE,
    INDEX idx_estado (estado),
    INDEX idx_fecha_creacion (fecha_creacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Configuracion
CREATE TABLE configuraciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor VARCHAR(500) NOT NULL,
    descripcion VARCHAR(255),
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modificado_por VARCHAR(100),
    INDEX idx_clave (clave)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Mensajes
CREATE TABLE mensajes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    remitente_id BIGINT NOT NULL,
    destinatario_id BIGINT NOT NULL,
    contenido TEXT NOT NULL,
    fecha_envio DATETIME NOT NULL,
    leido BOOLEAN NOT NULL DEFAULT FALSE,
    tipo_remitente VARCHAR(20),
    tipo_destinatario VARCHAR(20),
    FOREIGN KEY (remitente_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (destinatario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_destinatario_leido (destinatario_id, leido),
    INDEX idx_fecha_envio (fecha_envio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
