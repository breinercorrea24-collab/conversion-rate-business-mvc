CREATE TABLE IF NOT EXISTS simulacion (
    id_simulacion BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    date DATE NOT NULL,
    rate DECIMAL(10, 6) NOT NULL,
    rate_preferente DECIMAL(10, 6),
    exchange_amount DECIMAL(19, 2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_simulacion_cliente ON simulacion(id_cliente);
CREATE INDEX IF NOT EXISTS idx_simulacion_currency ON simulacion(currency);

-- Tabla cliente (usada por ClienteRepository)
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo_cliente VARCHAR(50),
    dni VARCHAR(50) UNIQUE,
    nombre VARCHAR(150),
    apellidos VARCHAR(150),
    tasa_preferencial DOUBLE
);

CREATE INDEX IF NOT EXISTS idx_cliente_dni ON cliente(dni);

-- Tabla cotizacion (usada por CotizacionRepository)
CREATE TABLE IF NOT EXISTS cotizacion (
    id_cotizacion BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_simulacion BIGINT,
    id_cliente BIGINT,
    amount DECIMAL(19,2),
    currency VARCHAR(3),
    target_currency VARCHAR(3),
    date DATE,
    status VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_cotizacion_simulacion ON cotizacion(id_simulacion);
CREATE INDEX IF NOT EXISTS idx_cotizacion_cliente ON cotizacion(id_cliente);

