CREATE DATABASE IF NOT EXISTS porteria_1
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE porteria_1;

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre_completo VARCHAR(120) NOT NULL,
  correo VARCHAR(120) NOT NULL UNIQUE,
  documento VARCHAR(20) NOT NULL UNIQUE,
  contrasena_hash VARCHAR(255) NOT NULL,
  numero_ficha VARCHAR(30) NULL,
  programa_formacion VARCHAR(180) NULL,
  jornada ENUM('Diurna','Nocturna','Fines de semana','Mixta') NULL,
  rol ENUM('Aprendiz','Instructor','Administrador') NOT NULL DEFAULT 'Aprendiz',
  estado ENUM('Activo','Pendiente','Inactivo') NOT NULL DEFAULT 'Pendiente',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sesiones_acceso (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  token VARCHAR(255) NOT NULL,
  remember_me TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  expires_at TIMESTAMP NULL,
  CONSTRAINT fk_sesiones_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    ON DELETE CASCADE
);
