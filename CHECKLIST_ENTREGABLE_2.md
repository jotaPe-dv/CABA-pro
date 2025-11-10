# ✅ CABA Pro - Checklist Entregable 2

## 📋 Requisitos del Entregable 2

### ✅ 1. API REST en Node.js/Express
- [x] Creada en repositorio separado `caba-arbitro-api/`
- [x] Solo funciones de árbitros (no administradores)
- [x] Consume API de Spring Boot
- [x] Endpoints implementados:
  - [x] Autenticación (login, register, refresh, logout)
  - [x] Perfil del árbitro
  - [x] Dashboard
  - [x] Asignaciones (ver, aceptar, rechazar)
  - [x] Liquidaciones
  - [x] Estadísticas

### ✅ 2. Dockerización
- [x] Dockerfile para Spring Boot
- [x] Dockerfile para Node.js
- [x] docker-compose.yml
- [x] .dockerignore files
- [x] Probado localmente con `docker-compose up`

### ⏳ 3. GitHub
- [ ] Crear repositorio separado para Node.js API
- [ ] Subir código de Node.js al nuevo repositorio
- [ ] Configurar GitHub Actions
- [ ] Agregar secrets de AWS

### ⏳ 4. Deployment en AWS
- [ ] Crear cuenta AWS (o usar la proporcionada)
- [ ] Crear repositorios ECR
- [ ] Crear cluster ECS
- [ ] Configurar task definitions
- [ ] Crear servicios ECS
- [ ] Configurar Load Balancer
- [ ] Deployment exitoso

### ⏳ 5. Dominio .tk
- [ ] Registrar dominio en Freenom
- [ ] Configurar DNS apuntando a AWS
- [ ] Verificar que funcione

## 📝 Pasos para Completar

### Paso 1: Probar Docker Localmente

```powershell
cd "c:\Users\Juan Rua\Desktop\CABA-pro"
.\test-docker.ps1
```

**Resultado esperado:** ✅ Todos los tests pasan

---

### Paso 2: Crear Repositorio en GitHub

1. **Ir a GitHub**: https://github.com/new

2. **Crear nuevo repositorio:**
   - Nombre: `caba-arbitro-api`
   - Descripción: `API REST Node.js/Express para árbitros - CABA Pro`
   - Público
   - NO inicializar con README

3. **Copiar URL del repositorio**: `https://github.com/tu-usuario/caba-arbitro-api.git`

4. **Subir código:**
```powershell
cd "c:\Users\Juan Rua\Desktop\CABA-pro\caba-arbitro-api"

# Inicializar git
git init

# Agregar archivos
git add .

# Commit inicial
git commit -m "Initial commit: Node.js API para árbitros CABA Pro"

# Agregar remote
git remote add origin https://github.com/tu-usuario/caba-arbitro-api.git

# Push
git push -u origin main
```

---

### Paso 3: Configurar AWS

#### 3.1. Crear Repositorios ECR

```powershell
# Login a AWS
aws configure

# Crear repositorio para Spring Boot
aws ecr create-repository `
    --repository-name caba-springboot-api `
    --region us-east-1

# Crear repositorio para Node.js
aws ecr create-repository `
    --repository-name caba-nodejs-api `
    --region us-east-1
```

#### 3.2. Crear Cluster ECS

```powershell
aws ecs create-cluster `
    --cluster-name caba-cluster `
    --region us-east-1
```

#### 3.3. Subir Imágenes a ECR

```powershell
# Login a ECR
$ecrUri = aws ecr describe-repositories --repository-names caba-springboot-api --query 'repositories[0].repositoryUri' --output text
$accountId = $ecrUri.Split('.')[0]
$region = "us-east-1"

aws ecr get-login-password --region $region | docker login --username AWS --password-stdin "$accountId.dkr.ecr.$region.amazonaws.com"

# Tag y push Spring Boot
docker tag caba-pro-caba:latest "$ecrUri:latest"
docker push "$ecrUri:latest"

# Tag y push Node.js
$ecrUriNode = aws ecr describe-repositories --repository-names caba-nodejs-api --query 'repositories[0].repositoryUri' --output text
docker tag caba-pro-caba-arbitro-api:latest "$ecrUriNode:latest"
docker push "$ecrUriNode:latest"
```

#### 3.4. Crear Task Definitions

Crear archivo `task-definition-springboot.json`:

```json
{
  "family": "caba-springboot-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [
    {
      "name": "springboot-api",
      "image": "TU_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/caba-springboot-api:latest",
      "portMappings": [
        {
          "containerPort": 8081,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/caba-springboot",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

Registrar task definition:
```powershell
aws ecs register-task-definition --cli-input-json file://task-definition-springboot.json
```

#### 3.5. Crear Servicios ECS

```powershell
aws ecs create-service `
    --cluster caba-cluster `
    --service-name caba-springboot-service `
    --task-definition caba-springboot-task `
    --desired-count 1 `
    --launch-type FARGATE `
    --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx],securityGroups=[sg-xxx],assignPublicIp=ENABLED}"
```

---

### Paso 4: Configurar GitHub Actions

1. **En GitHub, ir a Settings > Secrets and variables > Actions**

2. **Agregar secrets:**
   - `AWS_ACCESS_KEY_ID`: Tu access key
   - `AWS_SECRET_ACCESS_KEY`: Tu secret key

3. **Push para activar deployment:**
```powershell
git add .
git commit -m "Configure GitHub Actions for AWS deployment"
git push origin main
```

---

### Paso 5: Obtener Dominio .tk

1. **Ir a Freenom**: https://www.freenom.com

2. **Buscar dominio disponible:**
   - Ejemplo: `caba-arbitros.tk`
   - Click en "Get it now"
   - Checkout (gratuito por 12 meses)

3. **Configurar DNS:**
   - Services > My Domains > Manage Domain
   - Management Tools > Nameservers
   - Usar AWS Route53 o configurar A record:

```
Type: A
Name: @
TTL: 3600
Target: [IP del Load Balancer]
```

4. **Obtener IP del Load Balancer:**
```powershell
aws elbv2 describe-load-balancers `
    --query 'LoadBalancers[0].DNSName' `
    --output text
```

---

## 🎯 Entregables Finales

### Para entregar al docente:

1. **Link del repositorio Node.js:**
   ```
   https://github.com/tu-usuario/caba-arbitro-api
   ```

2. **Link del repositorio principal:**
   ```
   https://github.com/jotaPe-dv/CABA-pro
   ```

3. **Dominio .tk:**
   ```
   https://caba-arbitros.tk
   ```

4. **Endpoints de prueba:**
   ```
   https://caba-arbitros.tk/health
   https://caba-arbitros.tk/api/auth/login
   https://caba-arbitros.tk/api/arbitro/dashboard
   ```

---

## 📊 Verificación Final

```powershell
# Test completo
$DOMAIN = "tu-dominio.tk"

# 1. Health check
curl "https://$DOMAIN/health"

# 2. Login
$loginBody = @{
    email = "principal@caba.com"
    password = "123456"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "https://$DOMAIN/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginBody

$token = ($response.Content | ConvertFrom-Json).token

# 3. Dashboard
$headers = @{ "Authorization" = "Bearer $token" }
Invoke-WebRequest -Uri "https://$DOMAIN/api/arbitro/dashboard" `
    -Method GET `
    -Headers $headers
```

**Si todas las pruebas pasan:** ✅ Proyecto listo para entregar!

---

## ⏰ Timeline Estimado

- Docker local: 30 minutos ✅
- GitHub setup: 15 minutos
- AWS setup: 2-3 horas
- Dominio .tk: 30 minutos
- **Total: ~4 horas**

---

## 📞 Soporte

Si encuentras problemas, revisa:
- [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)
- [PRUEBAS_API_NODEJS.md](caba-arbitro-api/PRUEBAS_API_NODEJS.md)
- Logs de Docker: `docker-compose logs`
- Logs de AWS: `aws logs tail /ecs/caba-nodejs --follow`
