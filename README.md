# CABA Pro - Sistema de Gestión Integral de Arbitraje

![Logo del Equipo](./docs/images/caba-pro-logo.svg)

## Descripción del Proyecto

[cite_start]**CABA Pro** es un sistema nervioso central para instituciones de arbitraje de Baloncesto, diseñado para automatizar y centralizar sus operaciones diarias[cite: 3]. [cite_start]El objetivo es reemplazar procesos manuales como hojas de cálculo y grupos de WhatsApp por una plataforma web robusta, eficiente y escalable que gestiona desde la creación de perfiles de árbitros y la programación de partidos, hasta la liquidación de pagos y la comunicación oficial[cite: 4].

[cite_start]Este proyecto busca resolver la gestión manual, propensa a errores y dispersa, centralizando la información, automatizando la programación y simplificando la pre-liquidación de pagos para lograr mayor eficiencia, transparencia y una comunicación centralizada[cite: 24, 25, 26].

---

### Stack Tecnológico

* [cite_start]**Backend:** Spring Boot 3.x [cite: 72]
* [cite_start]**Base de Datos:** MySQL (alojada en AWS RDS) [cite: 72, 120]
* [cite_start]**Persistencia:** Spring Data JPA [cite: 75]
* [cite_start]**Seguridad:** Spring Security [cite: 96]
* [cite_start]**Frontend:** Thymeleaf [cite: 68]
* [cite_start]**Almacenamiento de Archivos:** AWS S3 [cite: 121]
* [cite_start]**Notificaciones:** AWS SES (opcional) [cite: 33]
* [cite_start]**Migraciones de BD:** Flyway / Liquibase (opcional) [cite: 78]

---

### Cómo Ejecutar el Proyecto

**Prerrequisitos:**
* Java JDK 17 o superior
* Maven 4.x o Gradle 8.x
* Acceso a una base de datos MySQL

**1. Clonar el repositorio:**
```bash
git clone URL_DEL_REPOSITORIO
cd NOMBRE_DEL_DIRECTORIO
