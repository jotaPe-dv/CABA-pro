# CABA Pro - Sistema de Gestión Integral de Arbitraje

![Logo del Equipo](./docs/images/caba-pro-logo.svg)

## Descripción del Proyecto

**CABA Pro** es un sistema nervioso central para instituciones de arbitraje de Baloncesto, diseñado para automatizar y centralizar sus operaciones diarias. El objetivo es reemplazar procesos manuales como hojas de cálculo y grupos de WhatsApp por una plataforma web robusta, eficiente y escalable que gestiona desde la creación de perfiles de árbitros y la programación de partidos, hasta la liquidación de pagos y la comunicación oficial.

Este proyecto busca resolver la gestión manual, propensa a errores y dispersa, centralizando la información, automatizando la programación y simplificando la pre-liquidación de pagos para lograr mayor eficiencia, transparencia y una comunicación centralizada.

## Arquitectura Técnica

### Tecnologías Utilizadas
- **Framework**: Spring Boot 3.5.5
- **Base de Datos**: MySQL (H2 para desarrollo)
- **ORM**: JPA/Hibernate
- **Validación**: Bean Validation
- **Java**: Versión 24

### Estructura del Proyecto
```
src/main/java/com/pagina/Caba/
├── config/          # Configuraciones (CORS, etc.)
├── controller/      # Controladores REST
├── dto/            # Data Transfer Objects
├── exception/      # Manejo global de excepciones
├── model/          # Entidades JPA
├── repository/     # Repositorios de datos
└── service/        # Servicios de negocio
```

## Entidades Implementadas

### Nuevas Entidades Agregadas

#### **Torneo**
- Gestión completa de torneos de baloncesto
- Fechas de inicio y fin
- Relaciones con Partidos y Tarifas
- Endpoints: `/api/torneos`

#### **Tarifa**
- Montos de pago por escalafón y torneo
- Validaciones de integridad de datos
- Búsqueda por múltiples criterios
- Endpoints: `/api/tarifas`

#### **Liquidación**
- Sistema automático de liquidaciones de pago
- Agrupación de asignaciones por árbitro
- Estados: PENDIENTE, PAGADA, ANULADA
- Cálculo automático de montos totales
- Endpoints: `/api/liquidaciones`

### Entidades Actualizadas

#### **Asignación**
- Actualizada con nuevas relaciones bidireccionales
- Soporte para liquidaciones
- Cambio de Float a BigDecimal para mayor precisión

#### **Arbitro**
- Relaciones con liquidaciones
- Validaciones mejoradas
- Métodos de conveniencia para relaciones

## API Endpoints Principales

### Torneos (/api/torneos)
- `GET /activos` - Torneos activos
- `POST /con-tarifas` - Crear torneo con tarifas
- `GET /buscar?nombre=` - Búsqueda por nombre

### Liquidaciones (/api/liquidaciones)
- `POST /generar` - Generar liquidación automática
- `PUT /{id}/aprobar` - Aprobar liquidación
- `GET /pendientes` - Liquidaciones pendientes

### Tarifas (/api/tarifas)
- `GET /torneo/{torneoId}` - Tarifas por torneo
- `GET /escalafon/{escalafon}` - Tarifas por escalafón

## Configuración y Ejecución

### Base de Datos
```properties
# H2 (Desarrollo - por defecto)
spring.datasource.url=jdbc:h2:mem:testdb
# Acceso: http://localhost:8080/h2-console

# MySQL (Producción)
spring.datasource.url=jdbc:mysql://localhost:3306/caba_pro
```

### Ejecución
```bash
cd CABA-pro/Caba
./mvnw spring-boot:run
```

### Acceso
- **API**: http://localhost:8080/api/
- **H2 Console**: http://localhost:8080/h2-console

## Características Técnicas Implementadas

✅ **Validaciones Bean Validation**  
✅ **Manejo global de excepciones**  
✅ **DTOs para transferencia de datos**  
✅ **Relaciones JPA bidireccionales**  
✅ **Configuración CORS**  
✅ **Logging estructurado**  
✅ **Arquitectura en capas (Controller-Service-Repository)**  

## Próximos Pasos de Desarrollo

- [ ] Sistema de autenticación JWT
- [ ] Documentación Swagger/OpenAPI
- [ ] Tests unitarios e integración
- [ ] Módulo de notificaciones
- [ ] Dashboard de estadísticas

---

**CABA Pro** - Automatizando el arbitraje deportivo con tecnología moderna

---

### Stack Tecnológico

* **Backend:** Spring Boot 3.x
* **Base de Datos:** MySQL (alojada en AWS RDS) 
* **Persistencia:** Spring Data JPA
* **Seguridad:** Spring Security 
* **Frontend:** Thymeleaf 
* **Almacenamiento de Archivos:** AWS S3 
* **Notificaciones:** AWS SES (opcional)
* **Migraciones de BD:** Flyway / Liquibase (opcional) 

---

### Cómo Ejecutar el Proyecto

**Prerrequisitos:**
* Java JDK 17 o superior
* Maven 4.x o Gradle 8.x
* Acceso a una base de datos MySQL

**1. Clonar el repositorio:**
```bash
git clone https://github.com/jotaPe-dv/CABA-pro.git
cd CABA-pro
