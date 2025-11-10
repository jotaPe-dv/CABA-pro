# Guía de Deployment - CABA Pro

Esta guía explica cómo desplegar la aplicación CABA Pro en AWS usando Docker y GitHub Actions.

## 📋 Arquitectura de Deployment

```
┌─────────────────────────────────────────────────────┐
│                  AWS Cloud                          │
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │          Application Load Balancer           │  │
│  │          (dominio.tk apunta aquí)           │  │
│  └────────────┬────────────────┬────────────────┘  │
│               │                │                    │
│  ┌────────────▼─────┐  ┌──────▼──────────┐         │
│  │   ECS Service    │  │   ECS Service   │         │
│  │   Node.js API    │  │ Spring Boot API │         │
│  │   Puerto: 3000   │  │   Puerto: 8081  │         │
│  └──────────────────┘  └─────────────────┘         │
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │          Amazon ECR (Docker Registry)        │  │
│  │  - caba-nodejs-api:latest                   │  │
│  │  - caba-springboot-api:latest               │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

## 🚀 Deployment Local con Docker

### 1. Construir y ejecutar con Docker Compose

```bash
# Construir imágenes
docker-compose build

# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

### 2. Verificar que funciona

```bash
# Spring Boot API
curl http://localhost:8081/actuator/health

# Node.js API
curl http://localhost:3000/health

# Login test
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "principal@caba.com", "password": "123456"}'
```

## ☁️ Deployment en AWS

### Prerequisitos en AWS

1. **Cuenta AWS** con permisos de:
   - ECR (Elastic Container Registry)
   - ECS (Elastic Container Service)
   - EC2 (Load Balancer)
   - IAM (Roles y políticas)

2. **Crear repositorios ECR:**
```bash
aws ecr create-repository --repository-name caba-springboot-api --region us-east-1
aws ecr create-repository --repository-name caba-nodejs-api --region us-east-1
```

3. **Crear cluster ECS:**
```bash
aws ecs create-cluster --cluster-name caba-cluster --region us-east-1
```

### Configuración de GitHub Secrets

En tu repositorio de GitHub, ve a **Settings > Secrets and variables > Actions** y agrega:

- `AWS_ACCESS_KEY_ID`: Tu Access Key de AWS
- `AWS_SECRET_ACCESS_KEY`: Tu Secret Key de AWS

### Deployment Automático

1. **Push a main:**
```bash
git add .
git commit -m "Deploy to AWS"
git push origin main
```

2. **GitHub Actions** automáticamente:
   - ✅ Construye las imágenes Docker
   - ✅ Las sube a ECR
   - ✅ Actualiza los servicios ECS
   - ✅ Espera a que el deployment esté estable

## 🌐 Configuración de Dominio .tk

### 1. Obtener dominio gratuito en Freenom

1. Ve a [Freenom](https://www.freenom.com)
2. Busca un dominio disponible (ejemplo: `caba-arbitros.tk`)
3. Regístralo gratuitamente (válido por 12 meses)

### 2. Configurar DNS

En Freenom, ve a **Services > My Domains > Manage Domain > Manage Freenom DNS**:

Agrega estos registros:

```
Type: A
Name: @
Target: [IP del Load Balancer de AWS]
TTL: 3600

Type: A
Name: www
Target: [IP del Load Balancer de AWS]
TTL: 3600
```

### 3. Obtener IP del Load Balancer

```bash
aws elbv2 describe-load-balancers --region us-east-1 \
  --query 'LoadBalancers[0].DNSName' --output text
```

## 📁 Estructura de Archivos Docker

```
CABA-pro/
├── Caba/
│   ├── Dockerfile              # Spring Boot
│   └── .dockerignore
├── caba-arbitro-api/
│   ├── Dockerfile              # Node.js
│   └── .dockerignore
├── docker-compose.yml          # Orquestación local
└── .github/
    └── workflows/
        └── deploy-aws.yml      # CI/CD Pipeline
```

## 🔧 Variables de Entorno

### Spring Boot API

```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:h2:mem:cabadb
JWT_SECRET=miClaveSecretaSuperSeguraParaJWT2024
```

### Node.js API

```env
NODE_ENV=production
PORT=3000
SPRING_API_URL=http://springboot-api:8081
JWT_SECRET=miClaveSecretaSuperSeguraParaJWT2024
```

## 🧪 Pruebas Post-Deployment

Una vez desplegado en AWS:

```bash
# Reemplaza con tu dominio
DOMAIN="tu-dominio.tk"

# Health check
curl https://$DOMAIN/health

# Login
curl -X POST https://$DOMAIN/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "principal@caba.com", "password": "123456"}'

# Dashboard (reemplaza TOKEN)
curl https://$DOMAIN/api/arbitro/dashboard \
  -H "Authorization: Bearer TOKEN"
```

## 📊 Monitoreo

### Ver logs en tiempo real:

```bash
# Spring Boot
aws logs tail /ecs/caba-springboot --follow

# Node.js
aws logs tail /ecs/caba-nodejs --follow
```

### Métricas de CloudWatch:

- CPU utilization
- Memory utilization
- Request count
- Error rate

## 🔄 Rollback

Si algo sale mal:

```bash
# Revertir a versión anterior
aws ecs update-service --cluster caba-cluster \
  --service caba-nodejs-service \
  --task-definition caba-nodejs:PREVIOUS_VERSION

# Forzar redespliegue
aws ecs update-service --cluster caba-cluster \
  --service caba-nodejs-service \
  --force-new-deployment
```

## 💰 Costos Estimados AWS

- **ECS Fargate**: ~$30-50/mes
- **ALB**: ~$20/mes
- **ECR**: ~$1/mes (primeros 500MB gratis)
- **CloudWatch Logs**: ~$5/mes
- **Total estimado**: $56-76/mes

## 📝 Checklist de Deployment

- [ ] Dockerfiles creados para ambas apps
- [ ] docker-compose.yml probado localmente
- [ ] GitHub Actions configurado
- [ ] Secrets de AWS agregados en GitHub
- [ ] Repositorios ECR creados
- [ ] Cluster ECS creado
- [ ] Task definitions creadas
- [ ] Services ECS creados
- [ ] Load Balancer configurado
- [ ] Dominio .tk obtenido
- [ ] DNS configurado apuntando a AWS
- [ ] HTTPS configurado (opcional con ACM)
- [ ] Pruebas de endpoints exitosas

## 🆘 Troubleshooting

### Error: "Unable to pull image"
```bash
# Verificar que las credenciales ECR estén correctas
aws ecr get-login-password --region us-east-1
```

### Error: "Service failed to stabilize"
```bash
# Ver eventos del servicio
aws ecs describe-services --cluster caba-cluster --services caba-nodejs-service
```

### Error: "Connection refused"
```bash
# Verificar security groups
# Asegúrate de que los puertos 3000 y 8081 estén abiertos
```

## 📚 Recursos Adicionales

- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [Docker Documentation](https://docs.docker.com/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Freenom DNS Guide](https://www.freenom.com/en/freehosting.html)

## 🤝 Soporte

Para problemas o preguntas, contacta al equipo de desarrollo.
