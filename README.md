# Microservicio de Cuentas y Movimientos

Este microservicio gestiona las cuentas bancarias y sus movimientos, permitiendo realizar operaciones CRUD y generación de reportes.

## Requisitos Previos

- Java 17 o superior
- Maven 3.8.x
- Docker y Docker Compose
- PostgreSQL (si se ejecuta localmente)

## Configuración del Entorno

1. Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```properties
# Application
SPRING_APPLICATION_NAME=msvc-account
SERVER_PORT=8002
API_PREFIX=/api

# Database
DB_HOST=
DB_PORT=
DB_NAME=
DB_USERNAME=
DB_PASSWORD=

# External Services
MICROSERVICE_CLIENTS_URL=http://localhost:8001
```

## Ejecución con Docker

1. Construir y levantar los contenedores:
```bash
docker-compose up --build -d
```

2. Verificar los logs de la aplicación:
```bash
docker-compose logs -f app
```

3. Detener los contenedores:
```bash
docker-compose down
```

## Ejecución Local

1. Compilar el proyecto:
```bash
mvn clean package -DskipTests
```

2. Ejecutar la aplicación:
```bash
mvn spring-boot:run
```

## Endpoints Disponibles

### Cuentas
- `GET /api/cuentas` - Listar todas las cuentas
- `GET /api/cuentas/{numeroCuenta}` - Obtener cuenta por número
- `POST /api/cuentas` - Crear nueva cuenta
- `PUT /api/cuentas/{numeroCuenta}` - Actualizar cuenta
- `DELETE /api/cuentas/{numeroCuenta}` - Eliminar cuenta

### Movimientos
- `GET /api/movimientos` - Listar todos los movimientos
- `GET /api/movimientos/{id}` - Obtener movimiento por ID
- `POST /api/movimientos` - Crear nuevo movimiento
- `PUT /api/movimientos/{id}` - Actualizar movimiento
- `DELETE /api/movimientos/{id}` - Eliminar movimiento

### Reportes
- `GET /api/movimientos/reportes` - Generar reporte de movimientos por cliente y fechas

## Estructura del Proyecto
```
ms-cuenta-movimiento/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/borja/springcloud/msvc/account/
│   │   └── resources/
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## Base de Datos

El servicio utiliza PostgreSQL como base de datos. La configuración se puede modificar en el archivo `.env`.

## Dependencias Principales

- Spring Boot 3.x
- Spring WebFlux
- Spring Data R2DBC
- PostgreSQL R2DBC Driver
- Project Reactor
- Lombok
- MapStruct

## Pruebas

Ejecutar las pruebas unitarias:
```bash
mvn test
```

## Documentación API

Una vez iniciada la aplicación, puedes acceder a la documentación de la API en:
- Swagger UI: `http://localhost:8002/swagger-ui.html`
- OpenAPI: `http://localhost:8002/v3/api-docs`