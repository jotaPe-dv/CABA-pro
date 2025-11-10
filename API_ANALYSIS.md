# 📊 Análisis de API REST - CABA Pro

**Fecha de Análisis:** 10 de Noviembre, 2025  
**Objetivo:** Documentar endpoints existentes y identificar endpoints faltantes para la API de árbitros

---

## 🎯 Resumen Ejecutivo

### ✅ **Endpoints Existentes:**
- ✅ ArbitroApiController - `/api/v1/arbitros/**`
- ✅ AsignacionApiController - `/api/v1/asignaciones/**`
- ✅ LiquidacionApiController - `/api/v1/liquidaciones/**`
- ✅ PartidoApiController - `/api/v1/partidos/**`
- ✅ TorneoApiController - `/api/v1/torneos/**`
- ✅ ReporteApiController - `/api/v1/reportes/**`
- ✅ WeatherApiController - `/api/weather/**`

### ❌ **Endpoints Faltantes:**
- ❌ **AuthApiController** - `/api/auth/**` (JWT Authentication)
- ❌ **ArbitroProfileApiController** - `/api/arbitro/**` (endpoints específicos para árbitro autenticado)
- ❌ **DashboardApiController** - `/api/arbitro/dashboard` (datos del dashboard)

---

## 📝 Inventario Detallado de Endpoints

### 1. **ArbitroApiController** ✅

**Ruta Base:** `/api/v1/arbitros`  
**CORS:** Habilitado para `localhost:3000` y `localhost:4200`

| Método | Endpoint | Descripción | Autorización | Estado |
|--------|----------|-------------|--------------|--------|
| GET | `/` | Listar árbitros (con filtros) | ADMIN | ✅ |
| GET | `/{id}` | Obtener árbitro por ID | ADMIN, ARBITRO | ✅ |
| PATCH | `/{id}/disponibilidad` | Actualizar disponibilidad | ADMIN, ARBITRO | ✅ |
| POST | `/` | Crear árbitro | ADMIN | ✅ |
| PUT | `/{id}` | Actualizar árbitro | ADMIN | ✅ |
| DELETE | `/{id}` | Eliminar (desactivar) árbitro | ADMIN | ✅ |

**Notas:**
- ✅ Retorna `ArbitroDto` con estadísticas calculadas
- ✅ Incluye filtros por disponibilidad y especialidad
- ✅ CORS configurado correctamente

---

### 2. **AsignacionApiController** ✅

**Ruta Base:** `/api/v1/asignaciones`  
**CORS:** Habilitado

| Método | Endpoint | Descripción | Autorización | Estado |
|--------|----------|-------------|--------------|--------|
| GET | `/` | Listar asignaciones | ADMIN | ✅ |
| GET | `/{id}` | Obtener asignación | ADMIN, ARBITRO | ✅ |
| POST | `/{id}/aceptar` | Aceptar asignación | ADMIN, ARBITRO | ✅ |
| POST | `/{id}/rechazar` | Rechazar asignación | ADMIN, ARBITRO | ⚠️ (verificar) |

**Notas:**
- ✅ Validación de estado antes de aceptar/rechazar
- ✅ Retorna `AsignacionDto`
- ⚠️ Falta endpoint para **obtener asignaciones de un árbitro específico**

---

### 3. **LiquidacionApiController** ✅

**Ruta Base:** `/api/v1/liquidaciones`  
**CORS:** Habilitado

| Método | Endpoint | Descripción | Autorización | Estado |
|--------|----------|-------------|--------------|--------|
| GET | `/` | Listar liquidaciones | ADMIN | ✅ |
| GET | `/{id}` | Obtener liquidación | ADMIN, ARBITRO | ✅ |
| POST | `/{id}/marcar-pagada` | Marcar como pagada | ADMIN | ✅ |

**Notas:**
- ✅ Filtro por estado pendiente
- ✅ Retorna `LiquidacionDto`
- ⚠️ Falta endpoint para **obtener liquidaciones de un árbitro específico**
- ⚠️ Falta endpoint para **descargar PDF**

---

### 4. **PartidoApiController** ✅

**Ruta Base:** `/api/v1/partidos`  
**CORS:** Habilitado

| Método | Endpoint | Descripción | Autorización | Estado |
|--------|----------|-------------|--------------|--------|
| GET | `/` | Listar partidos | ADMIN, ARBITRO | ✅ |
| GET | `/{id}` | Obtener partido | ADMIN, ARBITRO | ✅ |
| GET | `/{id}/clima` | Partido con clima | ADMIN, ARBITRO | ✅ |

**Notas:**
- ✅ Filtros por torneoId y estado completado
- ✅ Integración con WeatherService
- ✅ Retorna entidad `Partido` completa

---

## 🚨 Endpoints Críticos Faltantes

### ❌ **1. AuthApiController** (CRÍTICO)

**Ruta Base:** `/api/auth`

#### Endpoints Necesarios:

```java
POST /api/auth/login
Body: { "email": "arbitro@caba.com", "password": "123456" }
Response: { 
    "token": "eyJhbGc...", 
    "tipo": "Bearer",
    "arbitro": { ...ArbitroDto }
}
```

```java
POST /api/auth/register
Body: { 
    "email": "nuevo@caba.com",
    "password": "123456",
    "nombre": "Juan",
    "apellido": "Pérez",
    ...
}
Response: { 
    "token": "eyJhbGc...", 
    "arbitro": { ...ArbitroDto }
}
```

```java
POST /api/auth/refresh
Headers: { "Authorization": "Bearer <token>" }
Response: { "token": "eyJhbGc..." }
```

```java
POST /api/auth/logout
Headers: { "Authorization": "Bearer <token>" }
Response: { "message": "Logout exitoso" }
```

---

### ❌ **2. ArbitroProfileApiController** (CRÍTICO)

**Ruta Base:** `/api/arbitro`

#### Endpoints Necesarios:

```java
GET /api/arbitro/perfil
Headers: { "Authorization": "Bearer <token>" }
Response: { ...ArbitroDto completo del usuario autenticado }
```

```java
PUT /api/arbitro/perfil
Headers: { "Authorization": "Bearer <token>" }
Body: { "telefono": "...", "direccion": "...", ... }
Response: { ...ArbitroDto actualizado }
```

```java
GET /api/arbitro/asignaciones
Headers: { "Authorization": "Bearer <token>" }
Query Params: ?estado=PENDIENTE
Response: [ ...AsignacionDto[] del árbitro autenticado ]
```

```java
GET /api/arbitro/liquidaciones
Headers: { "Authorization": "Bearer <token>" }
Response: [ ...LiquidacionDto[] del árbitro autenticado ]
```

```java
GET /api/arbitro/liquidaciones/{id}/pdf
Headers: { "Authorization": "Bearer <token>" }
Response: PDF file (application/pdf)
```

```java
PUT /api/arbitro/disponibilidad
Headers: { "Authorization": "Bearer <token>" }
Body: { "disponible": true }
Response: { ...ArbitroDto actualizado }
```

---

### ❌ **3. DashboardApiController** (IMPORTANTE)

**Ruta Base:** `/api/arbitro/dashboard`

```java
GET /api/arbitro/dashboard
Headers: { "Authorization": "Bearer <token>" }
Response: {
    "totalAsignaciones": 25,
    "asignacionesPendientes": 3,
    "asignacionesAceptadas": 18,
    "asignacionesCompletadas": 15,
    "liquidacionesPendientes": 5,
    "totalGanado": 1500000,
    "proximosPartidos": [ ...Partido[] ],
    "estadisticas": {
        "partidosMes": 12,
        "promedioCalificacion": 4.5
    }
}
```

---

## 🔧 Dependencias Necesarias

### **Para JWT Authentication:**

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

---

## 📋 Plan de Implementación

### **Fase 1: JWT Authentication** (1-2 días)
- [ ] Agregar dependencias JWT al `pom.xml`
- [ ] Crear `JwtTokenProvider` utility class
- [ ] Crear `JwtAuthenticationFilter` para validar tokens
- [ ] Crear `AuthApiController` con endpoints de login/register/logout
- [ ] Crear DTOs: `LoginRequest`, `LoginResponse`, `RegisterRequest`
- [ ] Actualizar `SecurityConfig` para usar JWT

### **Fase 2: Endpoints Específicos para Árbitro** (2-3 días)
- [ ] Crear `ArbitroProfileApiController`
- [ ] Implementar endpoint `/api/arbitro/perfil` (GET y PUT)
- [ ] Implementar endpoint `/api/arbitro/asignaciones` (GET con filtros)
- [ ] Implementar endpoint `/api/arbitro/liquidaciones` (GET)
- [ ] Implementar endpoint `/api/arbitro/liquidaciones/{id}/pdf` (GET)
- [ ] Crear `DashboardService` para calcular estadísticas

### **Fase 3: Dashboard API** (1 día)
- [ ] Crear `DashboardApiController`
- [ ] Implementar lógica de agregación de datos
- [ ] Crear `DashboardDto` con todas las métricas
- [ ] Optimizar queries para performance

### **Fase 4: Testing y Documentación** (1 día)
- [ ] Probar todos los endpoints con Postman
- [ ] Actualizar Swagger/OpenAPI documentation
- [ ] Crear colección de Postman con ejemplos
- [ ] Documentar flujo de autenticación

---

## 🎯 Endpoints por Rol

### **Árbitro (ROLE_ARBITRO):**
```
POST   /api/auth/login
POST   /api/auth/register
POST   /api/auth/logout
GET    /api/arbitro/perfil
PUT    /api/arbitro/perfil
GET    /api/arbitro/dashboard
GET    /api/arbitro/asignaciones
GET    /api/arbitro/asignaciones/{id}
POST   /api/arbitro/asignaciones/{id}/aceptar
POST   /api/arbitro/asignaciones/{id}/rechazar
GET    /api/arbitro/liquidaciones
GET    /api/arbitro/liquidaciones/{id}
GET    /api/arbitro/liquidaciones/{id}/pdf
PUT    /api/arbitro/disponibilidad
GET    /api/v1/partidos (solo sus partidos asignados)
```

### **Administrador (ROLE_ADMIN):**
```
Todo lo anterior + endpoints CRUD completos de:
- /api/v1/arbitros/**
- /api/v1/asignaciones/**
- /api/v1/liquidaciones/**
- /api/v1/partidos/**
- /api/v1/torneos/**
- /api/v1/reportes/**
```

---

## 🔐 Configuración de Seguridad

### **SecurityConfig.java - Actualización Necesaria:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitado para API REST
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/auth/**").permitAll()
                
                // Endpoints de árbitro
                .requestMatchers("/api/arbitro/**").hasRole("ARBITRO")
                
                // Endpoints de admin
                .requestMatchers("/api/v1/arbitros/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/asignaciones/**").hasAnyRole("ADMIN", "ARBITRO")
                
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:4200"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

---

## 🧪 Testing

### **Postman Collection Structure:**

```
CABA Pro API
├── Auth
│   ├── Login (Árbitro)
│   ├── Login (Admin)
│   ├── Register
│   └── Logout
├── Árbitro
│   ├── Get Perfil
│   ├── Update Perfil
│   ├── Get Dashboard
│   ├── Get Asignaciones
│   ├── Aceptar Asignación
│   ├── Rechazar Asignación
│   ├── Get Liquidaciones
│   └── Download PDF Liquidación
└── Admin
    ├── CRUD Árbitros
    ├── CRUD Asignaciones
    ├── CRUD Liquidaciones
    └── Reportes
```

---

## 📊 Métricas de Cobertura

| Módulo | Endpoints Necesarios | Endpoints Existentes | Cobertura |
|--------|---------------------|---------------------|-----------|
| **Autenticación** | 4 | 0 | 0% ❌ |
| **Perfil Árbitro** | 6 | 0 | 0% ❌ |
| **Dashboard** | 1 | 0 | 0% ❌ |
| **Árbitros (CRUD)** | 6 | 6 | 100% ✅ |
| **Asignaciones** | 4 | 3 | 75% ⚠️ |
| **Liquidaciones** | 4 | 3 | 75% ⚠️ |
| **Partidos** | 3 | 3 | 100% ✅ |
| **TOTAL** | **28** | **15** | **54%** |

---

## 🚀 Siguiente Paso Recomendado

**Comenzar con JWT Authentication**, ya que es la base para todos los demás endpoints.

**Comando para agregar dependencias:**
```bash
# Ya está en el proyecto, solo ejecutar:
mvn clean install
```

¿Quieres que empiece a implementar el **AuthApiController** con JWT? 🔐
