# CABA Árbitro API - Node.js/Express

API REST en Node.js/Express que consume la API de Spring Boot de CABA Pro para gestión de árbitros.

## 🚀 Características

- ✅ Autenticación JWT (login, register, refresh, logout)
- ✅ Gestión de perfil de árbitro
- ✅ Dashboard con estadísticas completas
- ✅ Gestión de asignaciones (aceptar/rechazar)
- ✅ Consulta de liquidaciones
- ✅ CORS configurado
- ✅ Middleware de autenticación
- ✅ Validación de datos

## 📋 Requisitos previos

- Node.js v18 o superior
- API Spring Boot corriendo en `http://localhost:8081`

## 🔧 Instalación

1. Instalar dependencias:
```bash
npm install
```

2. Configurar variables de entorno:
```bash
cp .env.example .env
```

3. Editar `.env` con tus configuraciones

## ▶️ Ejecución

### Modo desarrollo (con nodemon):
```bash
npm run dev
```

### Modo producción:
```bash
npm start
```

La API estará disponible en: `http://localhost:3000`

## 📚 Endpoints disponibles

### Autenticación
- `POST /api/auth/login` - Login con email y password
- `POST /api/auth/register` - Registro de nuevo árbitro
- `POST /api/auth/refresh` - Refrescar token JWT
- `POST /api/auth/logout` - Cerrar sesión

### Perfil del Árbitro (requiere autenticación)
- `GET /api/arbitro/perfil` - Obtener perfil del árbitro autenticado
- `PUT /api/arbitro/perfil` - Actualizar perfil (teléfono, dirección, foto)
- `PUT /api/arbitro/disponibilidad` - Cambiar disponibilidad

### Asignaciones (requiere autenticación)
- `GET /api/arbitro/mis-asignaciones` - Ver mis asignaciones (opcional: ?estado=PENDIENTE)
- `POST /api/arbitro/asignacion/:id/aceptar` - Aceptar asignación
- `POST /api/arbitro/asignacion/:id/rechazar` - Rechazar asignación (body: {comentario})

### Liquidaciones (requiere autenticación)
- `GET /api/arbitro/mis-liquidaciones` - Ver mis liquidaciones (opcional: ?estado=PENDIENTE)

### Dashboard (requiere autenticación)
- `GET /api/arbitro/dashboard` - Dashboard completo con estadísticas
- `GET /api/arbitro/estadisticas` - Estadísticas mensuales (opcional: ?meses=3)

## 🔐 Autenticación

Todos los endpoints (excepto `/api/auth/*`) requieren token JWT en el header:

```http
Authorization: Bearer <tu_token_jwt>
```

## 📝 Ejemplo de uso

### 1. Login
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "principal@caba.com", "password": "123456"}'
```

### 2. Obtener perfil (con token)
```bash
curl -X GET http://localhost:3000/api/arbitro/perfil \
  -H "Authorization: Bearer <tu_token>"
```

### 3. Ver dashboard
```bash
curl -X GET http://localhost:3000/api/arbitro/dashboard \
  -H "Authorization: Bearer <tu_token>"
```

## 🛠️ Tecnologías

- **Express**: Framework web para Node.js
- **Axios**: Cliente HTTP para consumir Spring Boot API
- **jsonwebtoken**: Validación de tokens JWT
- **express-validator**: Validación de datos
- **cors**: Cross-Origin Resource Sharing
- **dotenv**: Gestión de variables de entorno
- **morgan**: Logger HTTP

## 📁 Estructura del proyecto

```
caba-arbitro-api/
├── src/
│   ├── config/
│   │   └── api.js              # Configuración de Axios
│   ├── middleware/
│   │   └── authMiddleware.js   # Validación JWT
│   ├── services/
│   │   ├── authService.js      # Consumo /api/auth/*
│   │   ├── arbitroService.js   # Consumo /api/arbitro/*
│   │   └── dashboardService.js # Consumo /api/arbitro/dashboard
│   ├── controllers/
│   │   ├── authController.js
│   │   ├── arbitroController.js
│   │   └── dashboardController.js
│   ├── routes/
│   │   ├── authRoutes.js
│   │   ├── arbitroRoutes.js
│   │   └── dashboardRoutes.js
│   └── app.js                  # Punto de entrada
├── .env
├── .env.example
├── .gitignore
├── package.json
└── README.md
```

## 🔄 Flujo de autenticación

1. Frontend hace login a Node.js API (`POST /api/auth/login`)
2. Node.js API reenvía credenciales a Spring Boot (`POST http://localhost:8081/api/auth/login`)
3. Spring Boot valida y retorna JWT token
4. Node.js API retorna el token al frontend
5. Frontend usa el token en todas las peticiones subsiguientes
6. Node.js API valida el token y reenvía peticiones a Spring Boot

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un Pull Request

## 📄 Licencia

ISC

## 👥 Autores

CABA Pro Team
