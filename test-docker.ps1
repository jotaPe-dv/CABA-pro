# Script para probar Docker localmente antes de deployment
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "CABA Pro - Prueba Docker Local" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Paso 1: Detener contenedores existentes
Write-Host "`n[1/5] Deteniendo contenedores existentes..." -ForegroundColor Yellow
docker-compose down -v 2>$null

# Paso 2: Construir imágenes
Write-Host "`n[2/5] Construyendo imágenes Docker..." -ForegroundColor Yellow
docker-compose build

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al construir imágenes" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Imágenes construidas exitosamente" -ForegroundColor Green

# Paso 3: Iniciar servicios
Write-Host "`n[3/5] Iniciando servicios..." -ForegroundColor Yellow
docker-compose up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al iniciar servicios" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Servicios iniciados" -ForegroundColor Green

# Paso 4: Esperar a que los servicios estén listos
Write-Host "`n[4/5] Esperando a que los servicios estén listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Verificar Spring Boot
$maxRetries = 12
$retries = 0
$springBootReady = $false

while ($retries -lt $maxRetries -and -not $springBootReady) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            $springBootReady = $true
            Write-Host "✅ Spring Boot API está listo" -ForegroundColor Green
        }
    }
    catch {
        $retries++
        Write-Host "  Esperando Spring Boot... ($retries/$maxRetries)" -ForegroundColor Gray
        Start-Sleep -Seconds 5
    }
}

if (-not $springBootReady) {
    Write-Host "❌ Spring Boot no respondió a tiempo" -ForegroundColor Red
    docker-compose logs springboot-api
    exit 1
}

# Verificar Node.js
$retries = 0
$nodejsReady = $false

while ($retries -lt $maxRetries -and -not $nodejsReady) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:3000/health" -UseBasicParsing -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            $nodejsReady = $true
            Write-Host "✅ Node.js API está listo" -ForegroundColor Green
        }
    }
    catch {
        $retries++
        Write-Host "  Esperando Node.js... ($retries/$maxRetries)" -ForegroundColor Gray
        Start-Sleep -Seconds 5
    }
}

if (-not $nodejsReady) {
    Write-Host "❌ Node.js no respondió a tiempo" -ForegroundColor Red
    docker-compose logs nodejs-api
    exit 1
}

# Paso 5: Ejecutar pruebas
Write-Host "`n[5/5] Ejecutando pruebas de integración..." -ForegroundColor Yellow

# Test 1: Login
Write-Host "`n  Test 1: Login..." -ForegroundColor Cyan
try {
    $loginBody = @{
        email = "principal@caba.com"
        password = "123456"
    } | ConvertTo-Json

    $loginResponse = Invoke-WebRequest -Uri "http://localhost:3000/api/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody `
        -UseBasicParsing

    $loginData = $loginResponse.Content | ConvertFrom-Json
    $token = $loginData.token
    
    Write-Host "    ✅ Login exitoso" -ForegroundColor Green
    Write-Host "    Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
}
catch {
    Write-Host "    ❌ Login falló: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Obtener perfil
Write-Host "`n  Test 2: Obtener perfil..." -ForegroundColor Cyan
try {
    $headers = @{
        "Authorization" = "Bearer $token"
    }

    $perfilResponse = Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/perfil" `
        -Method GET `
        -Headers $headers `
        -UseBasicParsing

    $perfil = $perfilResponse.Content | ConvertFrom-Json
    
    Write-Host "    ✅ Perfil obtenido: $($perfil.nombre) $($perfil.apellido)" -ForegroundColor Green
}
catch {
    Write-Host "    ❌ Error al obtener perfil: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 3: Dashboard
Write-Host "`n  Test 3: Dashboard..." -ForegroundColor Cyan
try {
    $dashboardResponse = Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/dashboard" `
        -Method GET `
        -Headers $headers `
        -UseBasicParsing

    $dashboard = $dashboardResponse.Content | ConvertFrom-Json
    
    Write-Host "    ✅ Dashboard obtenido" -ForegroundColor Green
    Write-Host "       - Total asignaciones: $($dashboard.resumen.totalAsignaciones)" -ForegroundColor Gray
    Write-Host "       - Pendientes: $($dashboard.resumen.pendientes)" -ForegroundColor Gray
}
catch {
    Write-Host "    ❌ Error al obtener dashboard: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 4: Asignaciones
Write-Host "`n  Test 4: Mis asignaciones..." -ForegroundColor Cyan
try {
    $asignacionesResponse = Invoke-WebRequest -Uri "http://localhost:3000/api/arbitro/mis-asignaciones" `
        -Method GET `
        -Headers $headers `
        -UseBasicParsing

    $asignaciones = $asignacionesResponse.Content | ConvertFrom-Json
    
    Write-Host "    ✅ Asignaciones obtenidas: $($asignaciones.total)" -ForegroundColor Green
}
catch {
    Write-Host "    ❌ Error al obtener asignaciones: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Resumen final
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "✅ TODAS LAS PRUEBAS EXITOSAS" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`nServicios corriendo:" -ForegroundColor Yellow
Write-Host "  - Spring Boot API: http://localhost:8081" -ForegroundColor White
Write-Host "  - Node.js API:     http://localhost:3000" -ForegroundColor White

Write-Host "`nComandos útiles:" -ForegroundColor Yellow
Write-Host "  - Ver logs:        docker-compose logs -f" -ForegroundColor White
Write-Host "  - Detener:         docker-compose down" -ForegroundColor White
Write-Host "  - Reiniciar:       docker-compose restart" -ForegroundColor White

Write-Host "`nListo para deployment en AWS! 🚀" -ForegroundColor Cyan
