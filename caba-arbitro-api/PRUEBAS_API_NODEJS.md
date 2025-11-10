# Guía de Pruebas - CABA Árbitro API (Node.js)

Esta guía te ayudará a probar la API de Node.js/Express que consume la API de Spring Boot.

## ⚙️ Prerequisitos

1. **Spring Boot API** corriendo en `http://localhost:8081`
2. **Node.js API** corriendo en `http://localhost:3000`

## 🧪 Pruebas con cURL (PowerShell)

### 1. Verificar que la API está corriendo

```powershell
curl http://localhost:3000/
```

**Respuesta esperada:**
```json
{
  "message": "CABA Árbitro API - Node.js/Express",
  "version": "1.0.0",
  "status": "online",
  "endpoints": {
    "auth": "/api/auth",
    "arbitro": "/api/arbitro",
    "dashboard": "/api/arbitro/dashboard"
  },
  "springBootAPI": "http://localhost:8081"
}
```

---

### 2. Login

```powershell
$body = @{
    email = "principal@caba.com"
    password = "123456"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:3000/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body

$response.Content | ConvertFrom-Json
```

**Guardar el token:**
```powershell
$token = ($response.Content | ConvertFrom-Json).token
Write-Host "Token guardado: $token"
```

---

### 3. Obtener perfil del árbitro

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/perfil" `
    -Method GET `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

### 4. Actualizar perfil

```powershell
$body = @{
    telefono = "3001234567"
    direccion = "Calle 123 #45-67"
    fotoUrl = "https://example.com/foto.jpg"
} | ConvertTo-Json

$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/perfil" `
    -Method PUT `
    -ContentType "application/json" `
    -Headers $headers `
    -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

### 5. Cambiar disponibilidad

```powershell
# Toggle (sin especificar valor)
$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/disponibilidad" `
    -Method PUT `
    -ContentType "application/json" `
    -Headers $headers `
    -Body "{}" | Select-Object -ExpandProperty Content | ConvertFrom-Json

# O especificar valor
$body = @{
    disponible = $true
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/disponibilidad" `
    -Method PUT `
    -ContentType "application/json" `
    -Headers $headers `
    -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

### 6. Ver mis asignaciones

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

# Todas las asignaciones
Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/mis-asignaciones" `
    -Method GET `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json

# Solo pendientes
Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/mis-asignaciones?estado=PENDIENTE" `
    -Method GET `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

### 7. Aceptar asignación

```powershell
$asignacionId = 1  # Cambiar por ID real

$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/asignacion/$asignacionId/aceptar" `
    -Method POST `
    -ContentType "application/json" `
    -Headers $headers `
    -Body "{}" | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

### 8. Rechazar asignación

```powershell
$asignacionId = 2  # Cambiar por ID real

$body = @{
    comentario = "No estoy disponible ese día"
} | ConvertTo-Json

$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/asignacion/$asignacionId/rechazar" `
    -Method POST `
    -ContentType "application/json" `
    -Headers $headers `
    -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

### 9. Ver mis liquidaciones

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

# Todas las liquidaciones
Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/mis-liquidaciones" `
    -Method GET `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json

# Solo pendientes
Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/mis-liquidaciones?estado=PENDIENTE" `
    -Method GET `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

### 10. Ver dashboard completo

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/dashboard" `
    -Method GET `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

### 11. Ver estadísticas mensuales

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

# Últimos 3 meses (default)
Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/estadisticas" `
    -Method GET `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json

# Últimos 6 meses
Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/estadisticas?meses=6" `
    -Method GET `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

### 12. Refresh token

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

$newToken = Invoke-WebRequest -Uri "http://localhost:3000/api/auth/refresh" `
    -Method POST `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json

$token = $newToken.token
Write-Host "Nuevo token: $token"
```

---

### 13. Logout

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-WebRequest -Uri "http://localhost:3000/api/auth/logout" `
    -Method POST `
    -Headers $headers | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

## 📊 Script completo de prueba

Aquí está un script completo que puedes ejecutar en PowerShell:

```powershell
# ========== PRUEBA COMPLETA DE LA API ==========

Write-Host "`n========== CABA Árbitro API - Pruebas ==========" -ForegroundColor Cyan

# 1. Verificar API
Write-Host "`n1. Verificando que la API esté corriendo..." -ForegroundColor Yellow
$info = Invoke-WebRequest -Uri "http://localhost:3000/" | ConvertFrom-Json
Write-Host "✅ API corriendo: $($info.status)" -ForegroundColor Green

# 2. Login
Write-Host "`n2. Haciendo login..." -ForegroundColor Yellow
$loginBody = @{
    email = "principal@caba.com"
    password = "123456"
} | ConvertTo-Json

$loginResponse = Invoke-WebRequest -Uri "http://localhost:3000/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginBody

$loginData = $loginResponse.Content | ConvertFrom-Json
$token = $loginData.token
Write-Host "✅ Login exitoso. Token obtenido." -ForegroundColor Green

# 3. Obtener perfil
Write-Host "`n3. Obteniendo perfil..." -ForegroundColor Yellow
$headers = @{ "Authorization" = "Bearer $token" }
$perfil = Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/perfil" `
    -Method GET -Headers $headers | ConvertFrom-Json
Write-Host "✅ Perfil: $($perfil.nombre) $($perfil.apellido)" -ForegroundColor Green

# 4. Ver dashboard
Write-Host "`n4. Obteniendo dashboard..." -ForegroundColor Yellow
$dashboard = Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/dashboard" `
    -Method GET -Headers $headers | ConvertFrom-Json
Write-Host "✅ Dashboard obtenido:" -ForegroundColor Green
Write-Host "   - Total asignaciones: $($dashboard.resumen.totalAsignaciones)"
Write-Host "   - Pendientes: $($dashboard.resumen.pendientes)"
Write-Host "   - Total ganado: `$$($dashboard.finanzas.totalGanado)"

# 5. Ver asignaciones
Write-Host "`n5. Obteniendo asignaciones..." -ForegroundColor Yellow
$asignaciones = Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/mis-asignaciones" `
    -Method GET -Headers $headers | ConvertFrom-Json
Write-Host "✅ Total de asignaciones: $($asignaciones.total)" -ForegroundColor Green

# 6. Ver estadísticas
Write-Host "`n6. Obteniendo estadísticas..." -ForegroundColor Yellow
$estadisticas = Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/estadisticas?meses=3" `
    -Method GET -Headers $headers | ConvertFrom-Json
Write-Host "✅ Estadísticas de $($estadisticas.periodo)" -ForegroundColor Green
Write-Host "   - Total ingresos: `$$($estadisticas.totalIngresos)"

Write-Host "`n========== TODAS LAS PRUEBAS EXITOSAS ==========" -ForegroundColor Cyan
```

---

## 🧪 Pruebas con Postman

Importa la colección de Postman que se encuentra en `docs/postman/CABA_Arbitro_API_NodeJS.postman_collection.json`

---

## ✅ Checklist de pruebas

- [ ] API corriendo en puerto 3000
- [ ] Login exitoso
- [ ] Obtener perfil
- [ ] Actualizar perfil
- [ ] Cambiar disponibilidad
- [ ] Ver asignaciones (todas)
- [ ] Ver asignaciones (filtradas por estado)
- [ ] Aceptar asignación
- [ ] Rechazar asignación
- [ ] Ver liquidaciones
- [ ] Ver dashboard
- [ ] Ver estadísticas
- [ ] Refresh token
- [ ] Logout

---

## 🐛 Solución de problemas

### Error: "Servicio no disponible"
- Verifica que Spring Boot esté corriendo en `http://localhost:8081`

### Error: "Token inválido"
- Verifica que el JWT_SECRET en `.env` coincida con el de Spring Boot

### Error de CORS
- Verifica que el frontend esté en una de las URLs permitidas en `app.js`

---

## 📝 Notas

- Todos los endpoints excepto `/api/auth/login` y `/api/auth/register` requieren token JWT
- El token debe enviarse en el header `Authorization: Bearer <token>`
- Los filtros por estado aceptan: `PENDIENTE`, `ACEPTADA`, `COMPLETADA`, `RECHAZADA`
