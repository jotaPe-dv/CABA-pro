# 🏀 CABA Árbitro API - Node.js/Express

API REST en Node.js/Express para la gestión de árbitros del sistema CABA Pro. Esta API consume la API de Spring Boot y proporciona endpoints específicos para las funcionalidades de los árbitros.

> **Nota**: Este repositorio es parte del **Entregable 2** del proyecto CABA Pro.

## 🌐 Demo en vivo

- **API Node.js**: https://tu-dominio.tk
- **Documentación**: https://tu-dominio.tk/docs
- **Health Check**: https://tu-dominio.tk/health

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Instalación](#instalación)
- [Uso](#uso)
- [Endpoints](#endpoints)
- [Deployment](#deployment)
- [Contribución](#contribución)

## ✨ Características

- ✅ **Autenticación JWT**: Login, registro, refresh y logout
- ✅ **Gestión de perfil**: Ver y actualizar información del árbitro
- ✅ **Dashboard completo**: Estadísticas, finanzas y próximos partidos
- ✅ **Asignaciones**: Ver, aceptar y rechazar asignaciones
- ✅ **Liquidaciones**: Consultar pagos y estados
- ✅ **Validación de datos**: express-validator
- ✅ **CORS configurado**: Para frontends en diferentes dominios
- ✅ **Logger HTTP**: Morgan para desarrollo y producción
- ✅ **Dockerizado**: Listo para deployment
- ✅ **CI/CD**: GitHub Actions para AWS

## 🛠️ Tecnologías

- **Runtime**: Node.js v18+
- **Framework**: Express.js
- **HTTP Client**: Axios
- **Autenticación**: jsonwebtoken
- **Validación**: express-validator
- **CORS**: cors
- **Logger**: morgan
- **Environment**: dotenv

## 🏗️ Arquitectura

```
┌─────────────┐      HTTP       ┌──────────────┐      HTTP      ┌─────────────┐
│   Frontend  │ ───────────────> │  Node.js API │ ─────────────> │ Spring Boot │
│  (Angular/  │    Port 3000    │   (Express)  │   Port 8081   │     API     │
│   React)    │ <─────────────── │              │ <───────────── │   (Java)    │
└─────────────┘      JSON        └──────────────┘      JSON      └─────────────┘
                                         │
                                         │ Validates JWT
                                         │ Forwards requests
                                         ▼
                                  ┌──────────────┐
                                  │  Middleware  │
                                  │ - Auth JWT   │
                                  │ - Validation │
                                  │ - Error Hdl  │
                                  └──────────────┘
```

## 📦 Instalación

### Prerequisitos

- Node.js 18+ instalado
- Spring Boot API corriendo en `http://localhost:8081`

### Pasos

1. **Clonar repositorio:**
```bash
git clone https://github.com/tu-usuario/caba-arbitro-api.git
cd caba-arbitro-api
```

2. **Instalar dependencias:**
```bash
npm install
```

3. **Configurar variables de entorno:**
```bash
cp .env.example .env
# Editar .env con tus configuraciones
```

4. **Iniciar servidor:**
```bash
# Desarrollo
npm run dev

# Producción
npm start
```

La API estará disponible en `http://localhost:3000`

## 🚀 Uso

### Autenticación

Todos los endpoints (excepto login y register) requieren un token JWT en el header:

```http
Authorization: Bearer <tu_token_jwt>
```

### Ejemplo de Login

```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "principal@caba.com",
    "password": "123456"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "expiresIn": 86400,
  "user": {
    "email": "principal@caba.com",
    "nombre": "Juan",
    "rol": "ARBITRO"
  }
}
```

## 📚 Endpoints

### Autenticación

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | Iniciar sesión | No |
| POST | `/api/auth/register` | Registrar nuevo árbitro | No |
| POST | `/api/auth/refresh` | Refrescar token | Sí |
| POST | `/api/auth/logout` | Cerrar sesión | Sí |

### Perfil del Árbitro

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/arbitro/perfil` | Ver perfil | Sí |
| PUT | `/api/arbitro/perfil` | Actualizar perfil | Sí |
| PUT | `/api/arbitro/disponibilidad` | Cambiar disponibilidad | Sí |

### Asignaciones

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/arbitro/mis-asignaciones` | Ver asignaciones | Sí |
| POST | `/api/arbitro/asignacion/:id/aceptar` | Aceptar asignación | Sí |
| POST | `/api/arbitro/asignacion/:id/rechazar` | Rechazar asignación | Sí |

### Liquidaciones

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/arbitro/mis-liquidaciones` | Ver liquidaciones | Sí |

### Dashboard

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/arbitro/dashboard` | Dashboard completo | Sí |
| GET | `/api/arbitro/estadisticas` | Estadísticas mensuales | Sí |

Ver documentación completa en [PRUEBAS_API_NODEJS.md](PRUEBAS_API_NODEJS.md)

## 🐳 Deployment con Docker

### Local

```bash
# Construir imagen
docker build -t caba-nodejs-api .

# Ejecutar contenedor
docker run -p 3000:3000 \
  -e SPRING_API_URL=http://host.docker.internal:8081 \
  caba-nodejs-api
```

### Con Docker Compose

```bash
docker-compose up -d
```

### AWS (con GitHub Actions)

1. **Configurar secrets en GitHub:**
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`

2. **Push a main:**
```bash
git push origin main
```

3. **GitHub Actions** automáticamente desplegará a AWS ECS

Ver guía completa en [DEPLOYMENT_GUIDE.md](../DEPLOYMENT_GUIDE.md)

## 🧪 Testing

```bash
# Ejecutar tests
npm test

# Con coverage
npm run test:coverage
```

## 📁 Estructura del Proyecto

```
caba-arbitro-api/
├── src/
│   ├── config/
│   │   └── api.js                 # Configuración Axios
│   ├── middleware/
│   │   └── authMiddleware.js      # Validación JWT
│   ├── services/
│   │   ├── authService.js         # Autenticación
│   │   ├── arbitroService.js      # Árbitros
│   │   └── dashboardService.js    # Dashboard
│   ├── controllers/
│   │   ├── authController.js
│   │   ├── arbitroController.js
│   │   └── dashboardController.js
│   ├── routes/
│   │   ├── authRoutes.js
│   │   ├── arbitroRoutes.js
│   │   └── dashboardRoutes.js
│   └── app.js                     # Aplicación principal
├── .env.example
├── .dockerignore
├── Dockerfile
├── package.json
└── README.md
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea tu feature branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es parte del curso de Desarrollo de Software de la Universidad.

## 👥 Autores

- **Equipo CABA Pro**
- Universidad: [Tu Universidad]
- Curso: Desarrollo de Software
- Entregable: 2

## 📞 Contacto

- Repositorio principal: [CABA-pro](https://github.com/jotaPe-dv/CABA-pro)
- Email: [tu-email@universidad.edu]

---

**Nota**: Esta API está diseñada específicamente para las funcionalidades de árbitros. No incluye funcionalidades de administrador por requisitos del proyecto.
