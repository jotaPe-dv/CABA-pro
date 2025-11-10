# CABA Pro - Sistema de Gestión Integral de Arbitraje

![Logo del Equipo](./docs/images/caba-pro-logo.svg)

## Descripción del Proyecto

**CABA Pro** es un sistema completo para instituciones de arbitraje de Baloncesto, diseñado para automatizar y centralizar sus operaciones diarias. El objetivo es reemplazar procesos manuales como hojas de cálculo y grupos de WhatsApp por una plataforma web robusta, eficiente y escalable que gestiona desde la creación de perfiles de árbitros y la programación de partidos, hasta la liquidación de pagos y la comunicación oficial.

## Arquitectura del Sistema

### Stack Tecnológico

#### Backend Principal (Spring Boot)
- **Framework**: Spring Boot 3.5.5
- **Java**: Versión 17
- **Base de Datos**: H2 (desarrollo) / MySQL (producción)
- **ORM**: JPA/Hibernate
- **Seguridad**: Spring Security + JWT
- **Puerto**: 8081

#### API de Árbitros (Node.js)
- **Framework**: Express.js
- **Runtime**: Node.js 18
- **Autenticación**: JWT (consume Spring Boot)
- **Puerto**: 3000

#### Infraestructura
- **Contenedores**: Docker + docker-compose
- **CI/CD**: GitHub Actions
- **Cloud**: AWS (ECS, ECR, RDS)

### Arquitectura de Servicios

```
Árbitros → Node.js API (3000) → Spring Boot API (8081) → Base de Datos
Admins → Spring Boot API (8081) → Base de Datos
```

**IMPORTANTE**: Los árbitros **SOLO** acceden a través de la API de Node.js. Los administradores usan directamente Spring Boot.

## Estructura del Proyecto

```
CABA-pro/
├── Caba/                           # Spring Boot API
│   ├── src/main/java/
│   │   └── com/pagina/Caba/
│   │       ├── config/             # Configuraciones (CORS, Security, JWT)
│   │       ├── controller/         # Controladores REST
│   │       ├── dto/                # Data Transfer Objects
│   │       ├── exception/          # Manejo de excepciones
│   │       ├── model/              # Entidades JPA
│   │       ├── repository/         # Repositorios
│   │       └── service/            # Lógica de negocio
│   ├── Dockerfile                  # Imagen Docker Spring Boot
│   └── pom.xml                     # Dependencias Maven
│
├── caba-arbitro-api/               # Node.js API (Árbitros)
│   ├── src/
│   │   ├── config/                 # Configuración Axios
│   │   ├── controllers/            # Controladores Express
│   │   ├── middleware/             # Autenticación JWT
│   │   ├── routes/                 # Rutas API
│   │   ├── services/               # Consumo Spring Boot
│   │   └── app.js                  # Punto de entrada
│   ├── Dockerfile                  # Imagen Docker Node.js
│   └── package.json                # Dependencias npm
│
├── docker-compose.yml              # Orquestación local
├── .github/workflows/              # CI/CD GitHub Actions
└── DEPLOYMENT_GUIDE.md            # Guía de despliegue AWS
```

## Entidades del Sistema

### Principales Entidades

#### **Torneo**
- Gestión completa de torneos de baloncesto
- Fechas de inicio y fin
- Relaciones con Partidos y Tarifas
- Endpoints: `/api/torneos/*`

#### **Tarifa**
- Montos de pago por escalafón y torneo
- Validaciones de integridad de datos
- Búsqueda por múltiples criterios
- Endpoints: `/api/tarifas/*`

#### **Liquidación**
- Sistema automático de liquidaciones de pago
- Agrupación de asignaciones por árbitro
- Estados: PENDIENTE, PAGADA, ANULADA
- Cálculo automático de montos totales
- Endpoints: `/api/liquidaciones/*`

#### **Asignación**
- Gestión de asignaciones de árbitros a partidos
- Estados: PENDIENTE, ACEPTADA, RECHAZADA
- Relaciones bidireccionales con Árbitro, Partido, Liquidación
- Endpoints: `/api/asignaciones/*`

#### **Arbitro**
- Perfiles completos de árbitros
- Escalafones: PRINCIPAL, AUXILIAR_1, AUXILIAR_2, COMISIONADO
- Disponibilidad y estado activo
- Endpoints: `/api/arbitros/*`

## API Endpoints

### Spring Boot API (8081)

#### Autenticación (/api/auth)
- `POST /login` - Login con email y password
- `POST /register` - Registro de nuevo árbitro
- `POST /refresh` - Refrescar token JWT
- `POST /logout` - Cerrar sesión

#### Administrador (/api/admin)
- `POST /usuarios` - Crear administrador
- `GET /usuarios` - Listar usuarios
- Gestión completa de árbitros, partidos, torneos

#### Árbitros (Solo desde Node.js)
- Ver perfil, asignaciones, liquidaciones
- Aceptar/rechazar asignaciones
- Dashboard con estadísticas

### Node.js API (3000)

Todos los endpoints de árbitros se consumen a través de Node.js:
- `/api/auth/*` - Autenticación
- `/api/arbitro/*` - Operaciones de árbitro
- Ver documentación completa en `caba-arbitro-api/README.md`


## Configuración y Ejecución

### Desarrollo Local con Docker (Recomendado)

1. **Clonar el repositorio:**
```bash
git clone https://github.com/jotaPe-dv/CABA-pro.git
cd CABA-pro
```

2. **Iniciar servicios con docker-compose:**
```bash
docker-compose up --build
```

3. **Acceder a las APIs:**
- Node.js API: http://localhost:3000
- Spring Boot API: http://localhost:8081
- H2 Console: http://localhost:8081/h2-console

### Desarrollo Local sin Docker

#### Spring Boot (Backend principal)
```bash
cd Caba
./mvnw spring-boot:run
```

#### Node.js API (Árbitros)
```bash
cd caba-arbitro-api
npm install
npm run dev
```

### Variables de Entorno

#### Spring Boot (`application.properties`)
```properties
# Base de datos
spring.datasource.url=jdbc:h2:mem:testdb
# JWT
jwt.secret=CABAProSecretKeyForJWTAuthentication2025...
# CORS
cors.allowed-origins=http://localhost:3000
```

#### Node.js (`.env`)
```
PORT=3000
SPRING_API_URL=http://localhost:8081
JWT_SECRET=CABAProSecretKeyForJWTAuthentication2025...
```

## Despliegue en AWS

Ver guía completa en `DEPLOYMENT_GUIDE.md` para:
- Configuración de ECR (repositorios de imágenes)
- Configuración de ECS (cluster y servicios)
- GitHub Actions (CI/CD automático)
- Configuración de dominio .tk

## Características Técnicas Implementadas

✅ **Autenticación JWT completa**  
✅ **Validaciones Bean Validation**  
✅ **Manejo global de excepciones**  
✅ **DTOs para transferencia de datos**  
✅ **Relaciones JPA bidireccionales**  
✅ **Configuración CORS**  
✅ **Arquitectura en capas**  
✅ **Docker y docker-compose**  
✅ **Node.js como gateway para árbitros**  
✅ **CI/CD con GitHub Actions**

## Testing

### Pruebas de integración local
Las APIs han sido probadas exitosamente con:
- Login y obtención de JWT
- Operaciones CRUD de todas las entidades
- Flujo completo de asignaciones
- Dashboard con estadísticas
- Comunicación Node.js → Spring Boot

### Datos de prueba
Usuario de prueba incluido en H2:
- Email: `principal@caba.com`
- Password: `123456`
- Rol: ARBITRO (PRINCIPAL)

---

**CABA Pro** - Automatizando el arbitraje deportivo con tecnología moderna

## Licencia

ISC

## Autores

CABA Pro Team
