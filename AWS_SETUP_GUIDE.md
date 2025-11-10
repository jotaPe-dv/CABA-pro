# Guía de Configuración AWS para CABA Pro

## ✅ Paso 1: Verificar requisitos previos

- [ ] Cuenta de AWS activa
- [ ] AWS CLI instalado y configurado
- [ ] Acceso IAM con permisos para: ECR, ECS, VPC, EC2, RDS, ELB

## 📦 Paso 2: Crear repositorios ECR (Elastic Container Registry)

Los repositorios ECR almacenarán las imágenes Docker de tus aplicaciones.

```bash
# Crear repositorio para Spring Boot API
aws ecr create-repository --repository-name caba-springboot-api --region us-east-1

# Crear repositorio para Node.js API
aws ecr create-repository --repository-name caba-nodejs-api --region us-east-1
```

**Anotar los URIs de los repositorios** (ejemplo):
- Spring Boot: `123456789012.dkr.ecr.us-east-1.amazonaws.com/caba-springboot-api`
- Node.js: `123456789012.dkr.ecr.us-east-1.amazonaws.com/caba-nodejs-api`

## 🗄️ Paso 3: Crear base de datos RDS (MySQL)

```bash
aws rds create-db-instance \
  --db-instance-identifier caba-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 8.0.35 \
  --master-username admin \
  --master-user-password TU_PASSWORD_SEGURO \
  --allocated-storage 20 \
  --vpc-security-group-ids sg-XXXXXXXXX \
  --db-subnet-group-name default \
  --backup-retention-period 7 \
  --region us-east-1 \
  --publicly-accessible
```

**Anotar:**
- Endpoint de la base de datos
- Puerto (3306)
- Usuario y contraseña

## 🌐 Paso 4: Crear VPC y Subnets (si no tienes una)

Si ya tienes una VPC configurada, salta este paso.

```bash
# Usar VPC por defecto o crear una nueva
aws ec2 describe-vpcs --region us-east-1
```

## 🔒 Paso 5: Configurar Security Groups

```bash
# Crear Security Group para ECS
aws ec2 create-security-group \
  --group-name caba-ecs-sg \
  --description "Security group for CABA Pro ECS services" \
  --vpc-id vpc-XXXXXXXXX \
  --region us-east-1

# Anotar el Security Group ID: sg-XXXXXXXXX

# Permitir tráfico HTTP (80)
aws ec2 authorize-security-group-ingress \
  --group-id sg-XXXXXXXXX \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0 \
  --region us-east-1

# Permitir tráfico HTTPS (443)
aws ec2 authorize-security-group-ingress \
  --group-id sg-XXXXXXXXX \
  --protocol tcp \
  --port 443 \
  --cidr 0.0.0.0/0 \
  --region us-east-1

# Permitir tráfico Spring Boot (8081) desde Node.js
aws ec2 authorize-security-group-ingress \
  --group-id sg-XXXXXXXXX \
  --protocol tcp \
  --port 8081 \
  --source-group sg-XXXXXXXXX \
  --region us-east-1

# Permitir tráfico Node.js (3000)
aws ec2 authorize-security-group-ingress \
  --group-id sg-XXXXXXXXX \
  --protocol tcp \
  --port 3000 \
  --cidr 0.0.0.0/0 \
  --region us-east-1
```

## 🚀 Paso 6: Crear ECS Cluster

```bash
aws ecs create-cluster \
  --cluster-name caba-cluster \
  --region us-east-1
```

## 📋 Paso 7: Crear Task Definitions

### Spring Boot Task Definition

Crear archivo `springboot-task-definition.json`:

```json
{
  "family": "caba-springboot-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::ACCOUNT_ID:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "springboot-api",
      "image": "ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/caba-springboot-api:latest",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 8081,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:mysql://caba-db.XXXXXXXXX.us-east-1.rds.amazonaws.com:3306/caba_pro"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "value": "admin"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "value": "TU_PASSWORD"
        },
        {
          "name": "JWT_SECRET",
          "value": "CABAProSecretKeyForJWTAuthentication2025ThisMustBeLongEnoughForHS512Algorithm"
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

### Node.js Task Definition

Crear archivo `nodejs-task-definition.json`:

```json
{
  "family": "caba-nodejs-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "executionRoleArn": "arn:aws:iam::ACCOUNT_ID:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "nodejs-api",
      "image": "ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/caba-nodejs-api:latest",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 3000,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "PORT",
          "value": "3000"
        },
        {
          "name": "SPRING_API_URL",
          "value": "http://springboot-service.caba-cluster.local:8081"
        },
        {
          "name": "JWT_SECRET",
          "value": "CABAProSecretKeyForJWTAuthentication2025ThisMustBeLongEnoughForHS512Algorithm"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/caba-nodejs",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

### Registrar las Task Definitions:

```bash
# Spring Boot
aws ecs register-task-definition \
  --cli-input-json file://springboot-task-definition.json \
  --region us-east-1

# Node.js
aws ecs register-task-definition \
  --cli-input-json file://nodejs-task-definition.json \
  --region us-east-1
```

## 🔄 Paso 8: Crear Application Load Balancer

```bash
# Crear ALB
aws elbv2 create-load-balancer \
  --name caba-alb \
  --subnets subnet-XXXXXXXXX subnet-YYYYYYYYY \
  --security-groups sg-XXXXXXXXX \
  --scheme internet-facing \
  --type application \
  --region us-east-1

# Anotar el ARN del ALB y el DNS name
```

### Crear Target Groups:

```bash
# Target Group para Spring Boot
aws elbv2 create-target-group \
  --name caba-springboot-tg \
  --protocol HTTP \
  --port 8081 \
  --vpc-id vpc-XXXXXXXXX \
  --target-type ip \
  --health-check-path /actuator/health \
  --region us-east-1

# Target Group para Node.js
aws elbv2 create-target-group \
  --name caba-nodejs-tg \
  --protocol HTTP \
  --port 3000 \
  --vpc-id vpc-XXXXXXXXX \
  --target-type ip \
  --health-check-path /health \
  --region us-east-1
```

### Crear Listeners:

```bash
# Listener HTTP (puerto 80) -> Node.js
aws elbv2 create-listener \
  --load-balancer-arn arn:aws:elasticloadbalancing:us-east-1:ACCOUNT_ID:loadbalancer/app/caba-alb/XXXXXXXXX \
  --protocol HTTP \
  --port 80 \
  --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:us-east-1:ACCOUNT_ID:targetgroup/caba-nodejs-tg/XXXXXXXXX \
  --region us-east-1

# Listener para Spring Boot (puerto 8081)
aws elbv2 create-listener \
  --load-balancer-arn arn:aws:elasticloadbalancing:us-east-1:ACCOUNT_ID:loadbalancer/app/caba-alb/XXXXXXXXX \
  --protocol HTTP \
  --port 8081 \
  --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:us-east-1:ACCOUNT_ID:targetgroup/caba-springboot-tg/XXXXXXXXX \
  --region us-east-1
```

## 🎯 Paso 9: Crear ECS Services

```bash
# Servicio Spring Boot
aws ecs create-service \
  --cluster caba-cluster \
  --service-name springboot-service \
  --task-definition caba-springboot-task \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-XXXXXXXXX,subnet-YYYYYYYYY],securityGroups=[sg-XXXXXXXXX],assignPublicIp=ENABLED}" \
  --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:us-east-1:ACCOUNT_ID:targetgroup/caba-springboot-tg/XXXXXXXXX,containerName=springboot-api,containerPort=8081" \
  --region us-east-1

# Servicio Node.js
aws ecs create-service \
  --cluster caba-cluster \
  --service-name nodejs-service \
  --task-definition caba-nodejs-task \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-XXXXXXXXX,subnet-YYYYYYYYY],securityGroups=[sg-XXXXXXXXX],assignPublicIp=ENABLED}" \
  --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:us-east-1:ACCOUNT_ID:targetgroup/caba-nodejs-tg/XXXXXXXXX,containerName=nodejs-api,containerPort=3000" \
  --region us-east-1
```

## 🔐 Paso 10: Configurar Secrets en GitHub

Ve a tu repositorio en GitHub: `https://github.com/jotaPe-dv/CABA-pro/settings/secrets/actions`

Agregar los siguientes secrets:

1. **AWS_ACCESS_KEY_ID**: Tu Access Key de AWS
2. **AWS_SECRET_ACCESS_KEY**: Tu Secret Access Key de AWS
3. **AWS_REGION**: `us-east-1` (o tu región)
4. **ECR_REPOSITORY_SPRINGBOOT**: `caba-springboot-api`
5. **ECR_REPOSITORY_NODEJS**: `caba-nodejs-api`
6. **ECS_CLUSTER**: `caba-cluster`
7. **ECS_SERVICE_SPRINGBOOT**: `springboot-service`
8. **ECS_SERVICE_NODEJS**: `nodejs-service`

## ✅ Paso 11: Verificar despliegue

Una vez configurado todo:

1. Push a GitHub activará el workflow de CI/CD
2. Verificar logs en GitHub Actions
3. Verificar servicios en ECS Console
4. Probar la API con el DNS del ALB

```bash
# Obtener DNS del ALB
aws elbv2 describe-load-balancers \
  --names caba-alb \
  --query 'LoadBalancers[0].DNSName' \
  --output text \
  --region us-east-1
```

Ejemplo de prueba:
```bash
curl http://caba-alb-XXXXXXXXX.us-east-1.elb.amazonaws.com/health
curl http://caba-alb-XXXXXXXXX.us-east-1.elb.amazonaws.com:8081/actuator/health
```

## 🌐 Paso 12: Configurar dominio .tk (opcional)

Ver `DOMAIN_SETUP.md` para instrucciones de configuración del dominio.

---

## 📝 Checklist de configuración

- [ ] ECR repositories creados
- [ ] RDS MySQL configurado
- [ ] Security Groups configurados
- [ ] ECS Cluster creado
- [ ] Task Definitions registradas
- [ ] Application Load Balancer creado
- [ ] Target Groups creados
- [ ] Listeners configurados
- [ ] ECS Services corriendo
- [ ] GitHub Secrets configurados
- [ ] CI/CD funcionando
- [ ] Dominio configurado (opcional)

## 🆘 Solución de problemas

### Los contenedores no inician
- Verificar logs en CloudWatch Logs
- Verificar permisos del execution role
- Verificar conectividad a RDS

### El ALB retorna 503
- Verificar que los targets estén healthy
- Verificar health check paths
- Verificar security groups

### GitHub Actions falla
- Verificar que todos los secrets estén configurados
- Verificar permisos IAM
- Revisar logs del workflow
