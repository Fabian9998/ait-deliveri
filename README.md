# Deliveri API

API REST para la gestion de conductores y ordenes de entrega

---

## Requisitos previos

- PostgreSQL.
- Docker.

## Configuracion de base de datos:


### 1.- Crear la base de datos en POSTGRESQL con el nombre sugerido: logistica_db
``` sql
create database logistica_db;
```
### 2.- Ejecutar escript de inicialización.
``` sql
CREATE TABLE driver (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    license_number VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE order_status (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(150) NOT NULL
);

INSERT INTO order_status (code, description) VALUES
('CREATED', 'Orden creada'),
('IN_TRANSIT', 'Orden en transito'),
('DELIVERED', 'Orden entregada'),
('CANCELLED', 'Orden cancelada');

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status INT NOT NULL,
    driver UUID NULL, 
    origin VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
	pdf VARCHAR(255) NULL,
	image VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_status
        FOREIGN KEY (status)
        REFERENCES order_status (id),

    CONSTRAINT fk_order_driver
        FOREIGN KEY (driver)
        REFERENCES driver (id)
        ON DELETE SET NULL
);
```
## Instalación y ejecución

### 1.- Clonar el proyecto:
``` bash
git clone https://github.com/Fabian9998/ait-deliveri.git
cd ait-deliveri/
```
### 2.- Contruir el contenedor:
``` bash
docker build -t ait \
  --build-arg PORT_SERVER=8090 \
  --build-arg DB_AIT_JDBC='jdbc:postgresql://localhost:5432/logistica_db' \
  --build-arg DB_AIT_USR='usuarioDB' \
  --build-arg DB_AIT_PASS='contraseñaDB' \
  --build-arg STORAGE_PATH='/app' \
  --progress=plain --no-cache .
```
> **Variables de construcción:**
>
> | Variable       | Descripción                        | Ejemplo                                              |
> |----------------|------------------------------------|------------------------------------------------------|
> | `PORT_SERVER`  | Puerto del servidor (opcional)     | `8090`                                               |
> | `DB_AIT_JDBC`  | URL JDBC de PostgreSQL             | `jdbc:postgresql://localhost:5432/logistica_db`      |
> | `DB_AIT_USR`   | Usuario de la base de datos        | `usuarioDB`                                          |
> | `DB_AIT_PASS`  | Contraseña de la base de datos     | `contraseñaDB`                                       |
> | `STORAGE_PATH` | Ruta para almacenar archivos       | `/app`  

### 3.- Correr el contenedor:
```bash
docker run -p 8090:8090 -v deliveri_storage:/app --name ait -i -t -d  ait
```
### 7.- Verificar que levantó correctamente:
```bash
docker logs -f ait
```
---
## Documentación
Acceder a Swagger una vez levantado el pryecto:
```
http://localhost:8090/swagger-ui/index.html#
```
---
## Autenticacion

Las API'S estan protegidas con **JWT**

### 1.- Obtener token:
```
POST /auth/login?username=admin&password=1234
```
> **Parámetros:**
>
> | Parámetro  | Tipo   | Valor   |
> |------------|--------|-------  |
> | `username` | String | 'admin' |
> | `password` | String | '1234'  |

### 2. Usar el token en cada petición

Agrega el siguiente header en todas las solicitudes:
```
Authorization: Bearer <tu-token>
```
---