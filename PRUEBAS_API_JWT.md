# 🧪 Guía de Pruebas - API REST con JWT

**Aplicación:** CABA-Pro  
**Puerto:** http://localhost:8081  
**Fecha:** 10 de Noviembre, 2025

---

## 📋 Tabla de Contenido
1. [Usuarios de Prueba](#usuarios-de-prueba)
2. [Endpoints de Autenticación](#endpoints-de-autenticación)
3. [Endpoints Protegidos](#endpoints-protegidos)
4. [Flujo Completo de Pruebas](#flujo-completo-de-pruebas)
5. [Errores Comunes](#errores-comunes)

---

## 👥 Usuarios de Prueba

### Árbitro Principal
```
Email: principal@caba.com
Password: arbitro123
Rol: ARBITRO
Especialidad: Principal
Escalafón: Nacional
```

### Árbitro Asistente
```
Email: asistente@caba.com
Password: arbitro123
Rol: ARBITRO
Especialidad: Auxiliar
Escalafón: Regional
```

### Árbitro de Mesa
```
Email: mesa@caba.com
Password: arbitro123
Rol: ARBITRO
Especialidad: Mesa
Escalafón: Local
```

### Administrador
```
Email: admin@caba.com
Password: admin123
Rol: ADMIN
```

---

## 🔐 Endpoints de Autenticación

### 1️⃣ **LOGIN - Obtener Token JWT**

**Método:** `POST`  
**URL:** `http://localhost:8081/api/auth/login`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "email": "principal@caba.com",
    "password": "arbitro123"
}
```

**Respuesta Exitosa (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmluY2lwYWxAY2FiYS5jb20iLCJpYXQiOjE3MzE1NjE2MzAsImV4cCI6MTczMTY0ODAzMH0.1vN5zQxLp...",
    "tipo": "Bearer",
    "arbitro": {
        "id": 1,
        "nombre": "Juan",
        "apellido": "Pérez",
        "email": "principal@caba.com",
        "telefono": "3001234567",
        "direccion": "Medellín",
        "numeroLicencia": "PRIN-001",
        "especialidad": "Principal",
        "escalafon": "Nacional",
        "disponible": true,
        "activo": true,
        "tarifaBase": 150000.00
    },
    "message": "Autenticación exitosa"
}
```

**Respuesta de Error (401 Unauthorized):**
```json
{
    "token": null,
    "tipo": null,
    "arbitro": null,
    "message": "Credenciales inválidas"
}
```

---

### 2️⃣ **REGISTER - Crear Nueva Cuenta**

**Método:** `POST`  
**URL:** `http://localhost:8081/api/auth/register`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "email": "nuevo.arbitro@caba.com",
    "password": "password123",
    "nombre": "Carlos",
    "apellido": "González",
    "telefono": "3009876543",
    "direccion": "Bogotá",
    "numeroLicencia": "ARB-2025-001",
    "especialidad": "Principal",
    "escalafon": "Nacional"
}
```

**Respuesta Exitosa (201 CREATED):**
```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tipo": "Bearer",
    "arbitro": {
        "id": 4,
        "nombre": "Carlos",
        "apellido": "González",
        "email": "nuevo.arbitro@caba.com",
        ...
    },
    "message": "Autenticación exitosa"
}
```

**Respuesta de Error (409 CONFLICT):**
```json
{
    "token": null,
    "tipo": null,
    "arbitro": null,
    "message": "Ya existe un usuario con ese email"
}
```

---

### 3️⃣ **REFRESH - Renovar Token**

**Método:** `POST`  
**URL:** `http://localhost:8081/api/auth/refresh`  
**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json
```

**Body:** (vacío)

**Respuesta Exitosa (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9.NEW_TOKEN...",
    "tipo": "Bearer",
    "arbitro": null,
    "message": null
}
```

---

### 4️⃣ **LOGOUT - Cerrar Sesión**

**Método:** `POST`  
**URL:** `http://localhost:8081/api/auth/logout`  
**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta Exitosa (200 OK):**
```json
{
    "message": "Logout exitoso"
}
```

---

## 🛡️ Endpoints Protegidos

### 5️⃣ **Listar Todos los Árbitros**

**Método:** `GET`  
**URL:** `http://localhost:8081/api/v1/arbitros`  
**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta Exitosa (200 OK):**
```json
[
    {
        "id": 1,
        "nombre": "Juan",
        "apellido": "Pérez",
        "email": "principal@caba.com",
        "especialidad": "Principal",
        "escalafon": "Nacional",
        "disponible": true
    },
    {
        "id": 2,
        "nombre": "Pedro",
        "apellido": "González",
        "email": "asistente@caba.com",
        "especialidad": "Auxiliar",
        "escalafon": "Regional",
        "disponible": true
    }
]
```

---

### 6️⃣ **Obtener Árbitro por ID**

**Método:** `GET`  
**URL:** `http://localhost:8081/api/v1/arbitros/1`  
**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta Exitosa (200 OK):**
```json
{
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "principal@caba.com",
    "telefono": "3001234567",
    "direccion": "Medellín",
    "numeroLicencia": "PRIN-001",
    "especialidad": "Principal",
    "escalafon": "Nacional",
    "disponible": true,
    "activo": true,
    "tarifaBase": 150000.00
}
```

---

### 7️⃣ **Listar Asignaciones del Árbitro**

**Método:** `GET`  
**URL:** `http://localhost:8081/api/v1/asignaciones/arbitro/1`  
**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta Exitosa (200 OK):**
```json
[
    {
        "id": 1,
        "partido": {
            "id": 1,
            "equipoLocal": "Lakers Los Angeles",
            "equipoVisitante": "76ers Philadelphia",
            "tipoPartido": "Cuartos de Final",
            "fechaPartido": "2025-11-15T19:00:00"
        },
        "arbitro": {
            "id": 1,
            "nombre": "Juan",
            "apellido": "Pérez"
        },
        "rolEspecifico": "Principal",
        "estado": "PENDIENTE",
        "montoCalculado": 150000.00,
        "fechaAsignacion": "2025-11-10T00:48:15"
    }
]
```

---

### 8️⃣ **Listar Liquidaciones**

**Método:** `GET`  
**URL:** `http://localhost:8081/api/v1/liquidaciones`  
**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta Exitosa (200 OK):**
```json
[]
```
*(Las liquidaciones se generan automáticamente cuando un partido se marca como completado)*

---

### 9️⃣ **Listar Partidos**

**Método:** `GET`  
**URL:** `http://localhost:8081/api/v1/partidos`  
**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta Exitosa (200 OK):**
```json
[
    {
        "id": 1,
        "equipoLocal": "Lakers Los Angeles",
        "equipoVisitante": "76ers Philadelphia",
        "tipoPartido": "Cuartos de Final",
        "ubicacion": "Medellín",
        "fechaPartido": "2025-11-15T19:00:00",
        "completado": false,
        "torneo": {
            "id": 1,
            "nombre": "Liga Nacional Baloncesto 2025",
            "activo": true
        }
    }
]
```

---

## 🔄 Flujo Completo de Pruebas

### **Paso 1: Login**
```bash
POST http://localhost:8081/api/auth/login
Body:
{
    "email": "principal@caba.com",
    "password": "arbitro123"
}

# Copiar el TOKEN de la respuesta
```

### **Paso 2: Usar Token en Endpoints Protegidos**
```bash
GET http://localhost:8081/api/v1/arbitros
Headers:
Authorization: Bearer <TOKEN_COPIADO>
```

### **Paso 3: Crear Nuevo Árbitro**
```bash
POST http://localhost:8081/api/auth/register
Body:
{
    "email": "test@caba.com",
    "password": "123456",
    "nombre": "Test",
    "apellido": "User",
    "numeroLicencia": "TEST-001",
    "telefono": "3001234567",
    "direccion": "Medellín",
    "especialidad": "Principal",
    "escalafon": "Nacional"
}
```

### **Paso 4: Verificar CORS (desde navegador)**
```javascript
// Abrir la consola del navegador en http://localhost:3000 y ejecutar:
fetch('http://localhost:8081/api/auth/login', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        email: 'principal@caba.com',
        password: 'arbitro123'
    })
})
.then(res => res.json())
.then(data => console.log(data))
.catch(err => console.error('Error CORS:', err));
```

---

## ❌ Errores Comunes

### **Error 401 - No Autorizado**
```json
{
    "timestamp": "2025-11-10T05:48:20.123",
    "status": 401,
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource",
    "path": "/api/v1/arbitros"
}
```
**Solución:** Incluir header `Authorization: Bearer <token>`

---

### **Error 403 - Acceso Denegado**
```json
{
    "timestamp": "2025-11-10T05:48:20.123",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied"
}
```
**Solución:** Verificar que el usuario tenga el ROL correcto (ARBITRO vs ADMIN)

---

### **Error 400 - Validación**
```json
{
    "token": null,
    "tipo": null,
    "arbitro": null,
    "message": "El email debe ser válido"
}
```
**Solución:** Verificar que todos los campos cumplan las validaciones:
- `email` debe ser válido
- `password` mínimo 6 caracteres
- Campos requeridos: nombre, apellido, numeroLicencia

---

### **Error CORS**
```
Access to fetch at 'http://localhost:8081/api/auth/login' from origin 'http://localhost:3000' 
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present
```
**Solución:** Verificar que en `application.properties` esté configurado:
```properties
cors.allowed-origins=http://localhost:3000,http://localhost:4200,http://localhost:5173
```

---

## 🎯 Verificación del Sistema

### ✅ **Checklist de Pruebas**

- [ ] **Login exitoso** con `principal@caba.com`
- [ ] **Token JWT** se genera correctamente
- [ ] **GET /api/v1/arbitros** funciona con token
- [ ] **Error 401** sin token
- [ ] **Register** crea nuevo usuario
- [ ] **Error 409** al registrar email duplicado
- [ ] **Refresh** renueva el token
- [ ] **Logout** limpia la sesión
- [ ] **CORS** permite peticiones desde localhost:3000
- [ ] **GET /api/v1/asignaciones/arbitro/1** retorna asignaciones
- [ ] **GET /api/v1/partidos** retorna partidos del torneo

---

## 🚀 Próximos Pasos

Una vez que todas las pruebas funcionen:

1. ✅ **Crear ArbitroProfileApiController**
   - `GET /api/arbitro/perfil` - Obtener perfil del usuario autenticado
   - `PUT /api/arbitro/perfil` - Actualizar perfil propio
   - `PUT /api/arbitro/disponibilidad` - Toggle disponibilidad

2. ✅ **Crear DashboardApiController**
   - `GET /api/arbitro/dashboard` - Estadísticas y próximos partidos

3. ✅ **Documentar en Swagger**
   - Agregar anotaciones `@Operation`, `@ApiResponse`
   - Configurar SecurityScheme para JWT

4. ✅ **Crear Node.js/Express API**
   - Consumir estos endpoints desde Node.js
   - Proxy para frontend React/Angular

---

## 📞 Soporte

Si encuentras algún problema:
1. Verificar logs de la consola de Spring Boot
2. Revisar que el puerto 8081 esté libre
3. Confirmar que H2 Database esté activa
4. Verificar que los datos de prueba se hayan cargado correctamente

**¡Todo listo para probar! 🎉**
