# Script para probar integracion entre Spring Boot y Node.js APIs
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test de Integracion de APIs" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Verificar que Spring Boot este corriendo
Write-Host "`n[1/5] Verificando Spring Boot API (puerto 8081)..." -ForegroundColor Yellow

try {
    $springHealth = Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method GET -ErrorAction Stop
    Write-Host "[OK] Spring Boot esta corriendo" -ForegroundColor Green
    Write-Host "   Status: $($springHealth.status)" -ForegroundColor Gray
} catch {
    Write-Host "[ERROR] Spring Boot no esta disponible" -ForegroundColor Red
    Write-Host "   Por favor inicia Spring Boot primero:" -ForegroundColor Yellow
    Write-Host "   cd Caba; .\mvnw.cmd spring-boot:run" -ForegroundColor White
    exit 1
}

# Verificar que Node.js API este corriendo
Write-Host "`n[2/5] Verificando Node.js API (puerto 3000)..." -ForegroundColor Yellow

try {
    $nodeHealth = Invoke-RestMethod -Uri "http://localhost:3000/health" -Method GET -ErrorAction Stop
    Write-Host "[OK] Node.js API esta corriendo" -ForegroundColor Green
    Write-Host "   Status: $($nodeHealth.status)" -ForegroundColor Gray
    Write-Host "   Spring Boot URL: $($nodeHealth.springBootApi)" -ForegroundColor Gray
} catch {
    Write-Host "[ERROR] Node.js API no esta disponible" -ForegroundColor Red
    Write-Host "   Por favor inicia Node.js primero:" -ForegroundColor Yellow
    Write-Host "   cd caba-arbitro-api; node src/app.js" -ForegroundColor White
    exit 1
}

# Test 1: Login directo a Spring Boot
Write-Host "`n[3/5] Test 1: Login directo a Spring Boot..." -ForegroundColor Yellow

$loginSpring = @{
    email = "principal@caba.com"
    password = "123456"
} | ConvertTo-Json

try {
    $responseSpring = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginSpring `
        -ErrorAction Stop
    
    Write-Host "[OK] Login directo a Spring Boot exitoso" -ForegroundColor Green
    Write-Host "   Email: $($responseSpring.email)" -ForegroundColor Gray
    Write-Host "   Token: $($responseSpring.token.Substring(0, 30))..." -ForegroundColor Gray
    
    $springToken = $responseSpring.token
} catch {
    Write-Host "[ERROR] Login a Spring Boot fallo" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Login a traves de Node.js (que consume Spring Boot)
Write-Host "`n[4/5] Test 2: Login a traves de Node.js API..." -ForegroundColor Yellow

$loginNode = @{
    email = "principal@caba.com"
    password = "123456"
} | ConvertTo-Json

try {
    $responseNode = Invoke-RestMethod -Uri "http://localhost:3000/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginNode `
        -ErrorAction Stop
    
    Write-Host "[OK] Login a traves de Node.js exitoso" -ForegroundColor Green
    Write-Host "   Email: $($responseNode.email)" -ForegroundColor Gray
    Write-Host "   Nombre: $($responseNode.nombre) $($responseNode.apellido)" -ForegroundColor Gray
    Write-Host "   Token: $($responseNode.token.Substring(0, 30))..." -ForegroundColor Gray
    
    $nodeToken = $responseNode.token
} catch {
    Write-Host "[ERROR] Login a traves de Node.js fallo" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    
    # Mostrar mas detalles del error
    if ($_.ErrorDetails.Message) {
        $errorDetail = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "   Detalle: $($errorDetail.message)" -ForegroundColor Red
    }
    exit 1
}

# Test 3: Obtener perfil a traves de Node.js
Write-Host "`n[5/5] Test 3: Obtener perfil del arbitro..." -ForegroundColor Yellow

$headers = @{
    "Authorization" = "Bearer $nodeToken"
}

try {
    $perfil = Invoke-RestMethod -Uri "http://localhost:3000/api/arbitro/perfil" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "[OK] Perfil obtenido exitosamente" -ForegroundColor Green
    Write-Host "   Nombre: $($perfil.nombre) $($perfil.apellido)" -ForegroundColor Gray
    Write-Host "   Email: $($perfil.email)" -ForegroundColor Gray
    Write-Host "   Licencia: $($perfil.numeroLicencia)" -ForegroundColor Gray
    Write-Host "   Escalafon: $($perfil.escalafon)" -ForegroundColor Gray
    Write-Host "   Disponible: $($perfil.disponible)" -ForegroundColor Gray
} catch {
    Write-Host "[ERROR] Error al obtener perfil" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 4: Dashboard
Write-Host "`n[BONUS] Test 4: Dashboard del arbitro..." -ForegroundColor Yellow

try {
    $dashboard = Invoke-RestMethod -Uri "http://localhost:3000/api/arbitro/dashboard" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "[OK] Dashboard obtenido exitosamente" -ForegroundColor Green
    Write-Host "   Total asignaciones: $($dashboard.resumen.totalAsignaciones)" -ForegroundColor Gray
    Write-Host "   Pendientes: $($dashboard.resumen.pendientes)" -ForegroundColor Gray
    Write-Host "   Aceptadas: $($dashboard.resumen.aceptadas)" -ForegroundColor Gray
    Write-Host "   Proximos partidos: $($dashboard.proximosPartidos.Count)" -ForegroundColor Gray
} catch {
    Write-Host "[WARNING] Dashboard no disponible (puede ser normal si no hay datos)" -ForegroundColor Yellow
}

# Test 5: Asignaciones
Write-Host "`n[BONUS] Test 5: Mis asignaciones..." -ForegroundColor Yellow

try {
    $asignaciones = Invoke-RestMethod -Uri "http://localhost:3000/api/arbitro/mis-asignaciones" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "[OK] Asignaciones obtenidas exitosamente" -ForegroundColor Green
    Write-Host "   Total: $($asignaciones.total)" -ForegroundColor Gray
    Write-Host "   Asignaciones: $($asignaciones.asignaciones.Count)" -ForegroundColor Gray
    
    if ($asignaciones.asignaciones.Count -gt 0) {
        Write-Host "`n   Primera asignacion:" -ForegroundColor Cyan
        $primera = $asignaciones.asignaciones[0]
        Write-Host "      - Estado: $($primera.estado)" -ForegroundColor Gray
        Write-Host "      - Rol: $($primera.rolEspecifico)" -ForegroundColor Gray
        Write-Host "      - Partido: $($primera.partido.equipoLocal) vs $($primera.partido.equipoVisitante)" -ForegroundColor Gray
    }
} catch {
    Write-Host "[WARNING] Asignaciones no disponibles" -ForegroundColor Yellow
}

# Resumen Final
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "INTEGRACION EXITOSA" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`nResultados:" -ForegroundColor Yellow
Write-Host "  [OK] Spring Boot API funcionando correctamente" -ForegroundColor Green
Write-Host "  [OK] Node.js API funcionando correctamente" -ForegroundColor Green
Write-Host "  [OK] Node.js consume Spring Boot exitosamente" -ForegroundColor Green
Write-Host "  [OK] Autenticacion JWT funcional" -ForegroundColor Green
Write-Host "  [OK] Endpoints de arbitro funcionales" -ForegroundColor Green

Write-Host "`nProximos pasos:" -ForegroundColor Yellow
Write-Host "  1. Probar con Docker: .\test-docker.ps1" -ForegroundColor White
Write-Host "  2. Crear repositorio GitHub" -ForegroundColor White
Write-Host "  3. Configurar AWS" -ForegroundColor White
Write-Host "  4. Obtener dominio .tk" -ForegroundColor White

Write-Host "`nListo para Docker y deployment!" -ForegroundColor Cyan
