# ARQUITECTURA DE APIS - IMPORTANTE

## 📋 Diagrama de Arquitectura

```
┌─────────────────────┐
│   USUARIOS          │
│   (Árbitros)        │
└──────────┬──────────┘
           │
           │ HTTP/HTTPS
           │
           ▼
┌─────────────────────┐
│   NODE.JS API       │
│   Puerto: 3000      │
│   ----------------  │
│   - Login           │
│   - Perfil          │
│   - Dashboard       │
│   - Asignaciones    │
│   - Liquidaciones   │
└──────────┬──────────┘
           │
           │ HTTP (interno)
           │
           ▼
┌─────────────────────┐
│  SPRING BOOT API    │
│  Puerto: 8081       │
│  ----------------   │
│  - Lógica negocio   │
│  - Base de datos    │
│  - Validación JWT   │
│  - Administradores  │
└─────────────────────┘
```

## 🎯 REGLAS DE USO

### ✅ CORRECTO

**Árbitros → Node.js API (Puerto 3000)**
```
https://tu-dominio.tk/api/auth/login
https://tu-dominio.tk/api/arbitro/perfil
https://tu-dominio.tk/api/arbitro/dashboard
https://tu-dominio.tk/api/arbitro/mis-asignaciones
```

**Administradores → Spring Boot (Puerto 8081)**
```
http://localhost:8081/admin/...
http://localhost:8081/arbitros/...
http://localhost:8081/torneos/...
```

### ❌ INCORRECTO

**NO hacer esto:**
```
❌ Árbitros llamando directamente a Spring Boot
❌ http://localhost:8081/api/arbitro/perfil
```

## 🔒 Seguridad

### Node.js API (Puerto 3000)
- ✅ Expuesta públicamente (internet)
- ✅ Solo endpoints de árbitros
- ✅ NO tiene acceso a funciones de administrador
- ✅ Pasa JWT a Spring Boot para validación

### Spring Boot API (Puerto 8081)
- ✅ Backend interno
- ✅ Toda la lógica de negocio
- ✅ Funciones de administradores
- ✅ Funciones de árbitros (consumidas por Node.js)
- ⚠️  En producción, puede estar en red privada

## 📝 Endpoints Disponibles

### Node.js API - SOLO para Árbitros

#### Autenticación
- `POST /api/auth/login` - Login de árbitro
- `POST /api/auth/register` - Registro de nuevo árbitro
- `POST /api/auth/refresh` - Refrescar token
- `POST /api/auth/logout` - Cerrar sesión

#### Perfil
- `GET /api/arbitro/perfil` - Ver perfil
- `PUT /api/arbitro/perfil` - Actualizar perfil
- `PUT /api/arbitro/disponibilidad` - Cambiar disponibilidad

#### Asignaciones
- `GET /api/arbitro/mis-asignaciones` - Ver asignaciones
- `POST /api/arbitro/asignaciones/:id/aceptar` - Aceptar asignación
- `POST /api/arbitro/asignaciones/:id/rechazar` - Rechazar asignación

#### Liquidaciones
- `GET /api/arbitro/liquidaciones` - Ver liquidaciones

#### Dashboard
- `GET /api/arbitro/dashboard` - Dashboard completo
- `GET /api/arbitro/estadisticas` - Estadísticas

#### Health
- `GET /health` - Estado del servicio

### Spring Boot API - Backend Completo

**Todas las funcionalidades del sistema:**
- Administradores
- Torneos
- Partidos
- Asignaciones (backend)
- Tarifas
- Liquidaciones (backend)
- Reportes
- etc.

## 🚀 Deployment

### Desarrollo Local
```bash
# Terminal 1: Spring Boot
cd Caba
.\mvnw.cmd spring-boot:run

# Terminal 2: Node.js
cd caba-arbitro-api
node src/app.js

# Terminal 3: Pruebas
.\test-api-integration.ps1
```

### Producción (AWS)

**Load Balancer configuración:**
```
Puerto 80/443 → Node.js (3000)
    ↓
Node.js consume Spring Boot (8081) internamente
```

**URLs finales:**
- Árbitros: `https://caba-arbitros.tk/*`
- Admin: `http://tu-servidor:8081/*` (red privada o VPN)

## ✅ Verificación

### Test de Integración
```powershell
.\test-api-integration.ps1
```

Debe mostrar:
```
[OK] Node.js consume Spring Boot exitosamente
[OK] Autenticacion JWT funcional
[OK] Endpoints de arbitro funcionales
```

### Test Manual
```bash
# 1. Login a través de Node.js
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"principal@caba.com","password":"123456"}'

# 2. Usar token para obtener perfil (Node.js → Spring Boot)
curl http://localhost:3000/api/arbitro/perfil \
  -H "Authorization: Bearer [TOKEN]"
```

## 📊 Monitoreo

### Health Checks
```bash
# Node.js
curl http://localhost:3000/health

# Spring Boot
curl http://localhost:8081/actuator/health
```

## 🔧 Configuración Importante

### Node.js `.env`
```env
PORT=3000
SPRING_API_URL=http://localhost:8081
NODE_ENV=development
```

### Spring Boot `application.properties`
```properties
server.port=8081
cors.allowed-origins=http://localhost:3000
jwt.secret=CABAProSecretKeyForJWTAuthentication2025...
```

## 📖 Documentación Relacionada

- [PRUEBAS_API_NODEJS.md](caba-arbitro-api/PRUEBAS_API_NODEJS.md)
- [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)
- [CHECKLIST_ENTREGABLE_2.md](CHECKLIST_ENTREGABLE_2.md)
