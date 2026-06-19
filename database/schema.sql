CREATE DATABASE IF NOT EXISTS porteria_1
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE porteria_1;

-- ============================================================
-- ROLES
-- ============================================================
CREATE TABLE IF NOT EXISTS roles (
  id   INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
);
INSERT IGNORE INTO roles (nombre) VALUES ('Admin'), ('Usuario');

-- ============================================================
-- USUARIOS
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
  id                       INT AUTO_INCREMENT PRIMARY KEY,
  nombre                   VARCHAR(150) NOT NULL,
  correo                   VARCHAR(120) NOT NULL UNIQUE,
  documento                VARCHAR(20)  NOT NULL UNIQUE,
  contrasena_hash          VARCHAR(255) NOT NULL,
  rol_id                   INT NOT NULL DEFAULT 2,
  cargo                    ENUM('Aprendiz','Instructor','Celador','Administrador') NOT NULL DEFAULT 'Aprendiz',
  perfil_completo          TINYINT(1)   NOT NULL DEFAULT 0,
  correo_verificado        TINYINT(1)   NOT NULL DEFAULT 0,
  codigo_verificacion      VARCHAR(10)  NULL,
  codigo_expiracion        DATETIME     NULL,
  intentos_fallidos        INT          NOT NULL DEFAULT 0,
  bloqueado_hasta          DATETIME     NULL,
  debe_cambiar_contrasena  TINYINT(1)   NOT NULL DEFAULT 0,
  codigo_recuperacion      VARCHAR(10)  NULL,
  recuperacion_expiracion  DATETIME     NULL,
  programa                 VARCHAR(180) NULL,
  ficha                    VARCHAR(30)  NULL,
  horario                  ENUM('Diurna','Nocturna','Fines de semana','Mixta') NULL,
  tipo_sangre              VARCHAR(5)   NULL,
  foto                     VARCHAR(255) NULL,
  session_token            VARCHAR(255) NULL,
  created_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- ============================================================
-- SESIONES DE ACCESO
-- ============================================================
CREATE TABLE IF NOT EXISTS sesiones_acceso (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id  INT          NOT NULL,
  token       VARCHAR(255) NOT NULL,
  remember_me TINYINT(1)   NOT NULL DEFAULT 0,
  created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  expires_at  TIMESTAMP    NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ============================================================
-- CARNETS
-- ============================================================
CREATE TABLE IF NOT EXISTS carnets (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ============================================================
-- CODIGOS QR
-- ============================================================
CREATE TABLE IF NOT EXISTS codigos_qr (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  carnet_id INT          NOT NULL,
  codigo    VARCHAR(255) NOT NULL UNIQUE,
  fecha     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (carnet_id) REFERENCES carnets(id) ON DELETE CASCADE
);

-- ============================================================
-- TURNOS CELADOR
-- ============================================================
CREATE TABLE IF NOT EXISTS turnos_celador (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  celador_id    INT      NOT NULL,
  fecha_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_salida  DATETIME NULL,
  estado        ENUM('Activo','Finalizado') NOT NULL DEFAULT 'Activo',
  FOREIGN KEY (celador_id) REFERENCES usuarios(id)
);

-- ============================================================
-- PUNTOS DE ACCESO
-- ============================================================
CREATE TABLE IF NOT EXISTS puntos_acceso (
  id     INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  tipo   VARCHAR(50)  NULL
);
INSERT IGNORE INTO puntos_acceso (id, nombre, tipo) VALUES (1, 'Entrada Principal', 'Principal');

-- ============================================================
-- ACCESOS (registro de movimientos)
-- ============================================================
CREATE TABLE IF NOT EXISTS accesos (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  punto_id         INT  NOT NULL DEFAULT 1,
  carnet_id        INT  NULL,
  referencia_id    INT  NULL,
  tipo_referencia  ENUM('Usuario','Visitante','Vehiculo','ObjetoExterno') NULL,
  tipo             ENUM('Entrada','Salida') NOT NULL,
  fecha            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  equipos_str      VARCHAR(500) NULL,
  FOREIGN KEY (punto_id)  REFERENCES puntos_acceso(id),
  FOREIGN KEY (carnet_id) REFERENCES carnets(id) ON DELETE SET NULL
);

-- ============================================================
-- AUDITORIA
-- ============================================================
CREATE TABLE IF NOT EXISTS auditoria (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id       INT          NULL,
  nombre_usuario   VARCHAR(150) NULL,
  tabla_afectada   VARCHAR(100) NULL,
  registro_id      INT          NULL,
  accion           VARCHAR(100) NULL,
  autorizado_por   VARCHAR(150) NULL,
  motivo           TEXT         NULL,
  detalles         TEXT         NULL,
  fecha            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- ============================================================
-- VISITANTES
-- ============================================================
CREATE TABLE IF NOT EXISTS visitantes (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  nombre         VARCHAR(150) NOT NULL,
  documento      VARCHAR(20)  NOT NULL UNIQUE,
  motivo         VARCHAR(300) NULL,
  qr_code        VARCHAR(255) NULL UNIQUE,
  activo         TINYINT(1)   NOT NULL DEFAULT 1,
  fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- VEHICULOS
-- ============================================================
CREATE TABLE IF NOT EXISTS vehiculos (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  placa      VARCHAR(20)  NOT NULL UNIQUE,
  tipo       ENUM('SENA','Externo') NOT NULL DEFAULT 'Externo',
  propietario VARCHAR(150) NULL,
  motivo     VARCHAR(300) NULL,
  qr_code    VARCHAR(255) NULL UNIQUE,
  activo     TINYINT(1)   NOT NULL DEFAULT 1
);

-- ============================================================
-- EQUIPOS (pertenecen a usuarios)
-- ============================================================
CREATE TABLE IF NOT EXISTS equipos (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  nombre     VARCHAR(150) NOT NULL,
  serial     VARCHAR(100) NULL UNIQUE,
  tipo       VARCHAR(100) NULL,
  estado     ENUM('Adentro','Afuera') NOT NULL DEFAULT 'Afuera',
  usuario_id INT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- ============================================================
-- OBJETOS EXTERNOS
-- ============================================================
CREATE TABLE IF NOT EXISTS objetos_externos (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  descripcion    VARCHAR(300) NOT NULL,
  serial         VARCHAR(100) NULL UNIQUE,
  propietario    VARCHAR(150) NULL,
  motivo         VARCHAR(300) NULL,
  activo         TINYINT(1)   NOT NULL DEFAULT 1,
  qr_code        VARCHAR(255) NULL UNIQUE,
  fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- ASISTENCIA A CLASES
-- ============================================================
CREATE TABLE IF NOT EXISTS asistencia_clases (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  instructor_id  INT        NOT NULL,
  aprendiz_id    INT        NOT NULL,
  ficha          VARCHAR(30) NOT NULL,
  fecha          DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  presente       TINYINT(1) NOT NULL DEFAULT 1,
  evaluacion     TEXT       NULL,
  FOREIGN KEY (instructor_id) REFERENCES usuarios(id),
  FOREIGN KEY (aprendiz_id)   REFERENCES usuarios(id)
);
