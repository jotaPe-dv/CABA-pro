# 🚀 Guía Rápida: Configurar RDS MySQL para CABA Pro

## ⏱️ Tiempo estimado: 15 minutos

## Paso 1: Crear la base de datos RDS

### Opción A - AWS Console (Recomendado):

1. **Ir a RDS Console:**
   ```
   https://console.aws.amazon.com/rds
   ```

2. **Click en "Create database"**

3. **Configuración básica:**
   - ☑️ **Standard create**
   - Motor: **MySQL**
   - Versión: **MySQL 8.0.35**
   - Plantilla: **Free tier** ⭐

4. **DB instance identifier:**
   ```
   caba-db
   ```

5. **Credenciales:**
   - Master username: `admin`
   - Master password: `CABAPro2025!`
   - Confirm password: `CABAPro2025!`
   
   📋 **ANOTA ESTO** - Lo necesitarás después

6. **DB instance class:**
   - ☑️ **db.t3.micro** (Elegible para capa gratuita)

7. **Storage:**
   - Allocated storage: **20 GB**
   - Storage type: **gp2** (SSD)
   - ☐ Enable storage autoscaling (desmarcar para capa gratuita)

8. **Connectivity:**
   - VPC: **(default)**
   - Subnet group: **(default)**
   - Public access: **Yes** ⚠️
   - VPC security group: **Create new** → Nombre: `caba-db-sg`

9. **Additional configuration (expandir):**
   - ⚠️ **MUY IMPORTANTE** ⚠️
   - Initial database name: `caba_pro`
   - Backup retention period: **7 days**
   - ☑️ Enable automated backups
   - ☑️ Enable encryption

10. **Click "Create database"**
    - ⏳ Espera 5-10 minutos...
    - ☕ Tómate un café mientras tanto

## Paso 2: Configurar Security Group

Mientras se crea la BD, vamos a configurar el acceso:

1. **Ir a EC2 Console → Security Groups:**
   ```
   https://console.aws.amazon.com/ec2/home#SecurityGroups
   ```

2. **Buscar:** `caba-db-sg`

3. **Click en el Security Group → Inbound rules → Edit inbound rules**

4. **Add rule:**
   - Type: **MySQL/Aurora**
   - Protocol: **TCP**
   - Port: **3306**
   - Source: **Custom** → `0.0.0.0/0` (Para desarrollo)
   
   ⚠️ **Para producción:** Usar solo el security group de ECS

5. **Save rules**

## Paso 3: Obtener el Endpoint

1. **Volver a RDS Console**

2. **Click en `caba-db`**

3. **Esperar hasta que el Status sea:** `Available` ✅

4. **En la sección "Connectivity & security", copiar:**
   
   **Endpoint:**
   ```
   caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com
   ```
   
   📋 **ANOTA ESTO** - Es la URL de conexión

## Paso 4: Probar la conexión (Opcional)

### Opción A - MySQL Workbench:

1. Abrir MySQL Workbench
2. Nueva conexión:
   - Hostname: `caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com`
   - Port: `3306`
   - Username: `admin`
   - Password: `CABAPro2025!`
3. Test Connection
4. Si conecta ✅, ¡perfecto!

### Opción B - Línea de comandos:

```bash
mysql -h caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com -u admin -p
# Ingresar password: CABAPro2025!
```

Si conecta, verás:
```
mysql>
```

## Paso 5: Inicializar la base de datos

### Opción A - Desde MySQL Workbench:

1. Conectar a la BD
2. Abrir el archivo: `database/init-db.sql`
3. Ejecutar todo (Ctrl + Shift + Enter)
4. Verificar mensaje: ✅ `Base de datos inicializada correctamente!`

### Opción B - Desde terminal:

```powershell
# Navegar a la carpeta del proyecto
cd "C:\Users\Juan Rua\Desktop\CABA-pro"

# Ejecutar el script
Get-Content database\init-db.sql | mysql -h caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com -u admin -pCABAPro2025! caba_pro
```

### Opción C - Dejar que Spring Boot lo haga:

Spring Boot creará las tablas automáticamente con `ddl-auto=update`.

**PERO** necesitarás insertar los datos de prueba manualmente después.

## Paso 6: Configurar Spring Boot

Actualizar las variables de entorno para ECS Task Definition:

```json
{
  "name": "SPRING_DATASOURCE_URL",
  "value": "jdbc:mysql://caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com:3306/caba_pro"
},
{
  "name": "SPRING_DATASOURCE_USERNAME",
  "value": "admin"
},
{
  "name": "SPRING_DATASOURCE_PASSWORD",
  "value": "CABAPro2025!"
},
{
  "name": "SPRING_JPA_HIBERNATE_DDL_AUTO",
  "value": "update"
}
```

## ✅ Checklist Final

- [ ] Base de datos RDS creada
- [ ] Status: Available
- [ ] Security Group configurado (puerto 3306 abierto)
- [ ] Endpoint anotado
- [ ] Conexión probada exitosamente
- [ ] Script `init-db.sql` ejecutado
- [ ] Datos de prueba insertados
- [ ] Variables de entorno actualizadas

## 📋 Información para Referencia

Guarda esto para después:

```
RDS Endpoint: caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com
Puerto: 3306
Base de datos: caba_pro
Usuario: admin
Password: CABAPro2025!

JDBC URL: jdbc:mysql://caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com:3306/caba_pro
```

## 🆘 Solución de Problemas

### Error: "Can't connect to MySQL server"
- ✅ Verificar que el Security Group permite el puerto 3306
- ✅ Verificar que Public Access está en "Yes"
- ✅ Esperar a que el Status sea "Available"

### Error: "Access denied"
- ✅ Verificar usuario y contraseña
- ✅ Asegurarse de usar el master username correcto

### Error: "Unknown database 'caba_pro'"
- ✅ Verificar que pusiste el "Initial database name" al crear RDS
- ✅ Si no lo pusiste, crear la BD manualmente:
  ```sql
  CREATE DATABASE caba_pro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

## 💰 Costos Estimados

**Con Free Tier (primer año):**
- 750 horas/mes de db.t3.micro = **$0** ✅
- 20 GB de almacenamiento = **$0** ✅
- Backups hasta 20 GB = **$0** ✅

**Después del Free Tier:**
- db.t3.micro = ~$15/mes
- 20 GB storage = ~$2/mes
- **Total: ~$17/mes**

## 🎉 ¡Listo!

Tu base de datos MySQL en AWS RDS está configurada y lista para usar con CABA Pro.

**Siguiente paso:** Continuar con la configuración de ECS (ver `AWS_SETUP_GUIDE.md`)
