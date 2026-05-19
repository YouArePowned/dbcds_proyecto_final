CREATE TABLE IF NOT EXISTS plantas (
    id BIGINT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    latitud DOUBLE,
    longitud DOUBLE
);

CREATE TABLE IF NOT EXISTS camas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    planta_id BIGINT,
    ocupada BOOLEAN DEFAULT FALSE,
    latitud DOUBLE,
    longitud DOUBLE,
    FOREIGN KEY (planta_id) REFERENCES plantas(id)
);

CREATE TABLE IF NOT EXISTS pacientes (
    dni VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(200),
    genero VARCHAR(10),
    grado VARCHAR(20),
    estado VARCHAR(20),
    cama_id BIGINT,
    FOREIGN KEY (cama_id) REFERENCES camas(id)
);
