# CABA Pro - Sistema de Gestión Integral de Arbitraje

![Logo del Equipo](./docs/images/caba-pro-logo.svg)

## Descripción del Proyecto

**CABA Pro** es un sistema nervioso central para instituciones de arbitraje de Baloncesto, diseñado para automatizar y centralizar sus operaciones diarias. El objetivo es reemplazar procesos manuales como hojas de cálculo y grupos de WhatsApp por una plataforma web robusta, eficiente y escalable que gestiona desde la creación de perfiles de árbitros y la programación de partidos, hasta la liquidación de pagos y la comunicación oficial.

Este proyecto busca resolver la gestión manual, propensa a errores y dispersa, centralizando la información, automatizando la programación y simplificando la pre-liquidación de pagos para lograr mayor eficiencia, transparencia y una comunicación centralizada.

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
