# Base de Datos CABA Pro

## 📋 Información General

**Motor:** MySQL 8.0.35  
**Nombre de BD:** `caba_pro`  
**Puerto:** 3306  
**Charset:** utf8mb4_unicode_ci

## 🗄️ Estructura de Tablas

### Tablas Principales:

1. **usuario** - Tabla base para todos los usuarios (herencia)
2. **arbitro** - Información específica de árbitros
3. **administrador** - Información específica de administradores
4. **torneo** - Torneos de baloncesto
5. **tarifa** - Tarifas por escalafón y torneo
6. **partido** - Partidos programados
7. **asignacion** - Asignaciones de árbitros a partidos
8. **liquidacion** - Liquidaciones de pago

## 🚀 Inicialización de la Base de Datos

### Opción 1: Desde MySQL Workbench

1. Conectar a RDS:
   - Host: `caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com`
   - Port: `3306`
   - Username: `admin`
   - Password: `CABAPro2025!`

2. Abrir el archivo `init-db.sql`

3. Ejecutar todo el script (Ctrl + Shift + Enter)

4. Verificar que aparezca: `✅ Base de datos inicializada correctamente!`

### Opción 2: Desde línea de comandos

```bash
# Windows (PowerShell)
Get-Content init-db.sql | mysql -h caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com -u admin -pCABAPro2025! caba_pro

# Linux/Mac
mysql -h caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com -u admin -pCABAPro2025! caba_pro < init-db.sql
```

### Opción 3: Dejar que Spring Boot lo haga automáticamente

Spring Boot está configurado con `spring.jpa.hibernate.ddl-auto=update`, lo que significa que:
- ✅ Creará las tablas automáticamente si no existen
- ✅ Actualizará las tablas si hay cambios en las entidades
- ⚠️ **PERO NO insertará datos iniciales**

Para datos iniciales, ejecuta manualmente solo la sección de INSERT del script.

## 👥 Usuarios de Prueba

El script `init-db.sql` crea los siguientes usuarios:

### Árbitros:

| Email | Password | Rol | Escalafón |
|-------|----------|-----|-----------|
| principal@caba.com | 123456 | ARBITRO | PRINCIPAL |
| auxiliar1@caba.com | 123456 | ARBITRO | AUXILIAR_1 |
| auxiliar2@caba.com | 123456 | ARBITRO | AUXILIAR_2 |

### Administradores:

| Email | Password | Rol | Cargo |
|-------|----------|-----|-------|
| admin@caba.com | 123456 | ADMINISTRADOR | Administrador General |

**⚠️ IMPORTANTE:** Cambiar estas contraseñas en producción.

## 📊 Datos de Prueba Incluidos

- ✅ 1 Torneo activo: "Liga Nacional 2025"
- ✅ 4 Tarifas (una por cada escalafón)
- ✅ 3 Partidos programados
- ✅ 3 Asignaciones pendientes (para el primer partido)

### Partidos de Prueba:

1. **Bulls Chicago vs Lakers Los Angeles**  
   Fecha: 15/11/2025 19:00  
   Ubicación: Arena Bogotá

2. **Heat Miami vs Warriors Golden State**  
   Fecha: 16/11/2025 20:00  
   Ubicación: Coliseo Medellín

3. **Celtics Boston vs Nets Brooklyn**  
   Fecha: 17/11/2025 18:30  
   Ubicación: Estadio Cali

## 🔍 Queries Útiles

### Ver todos los árbitros:
```sql
SELECT u.nombre, u.email, a.escalafon, a.disponible, a.total_partidos, a.total_ingresos
FROM usuario u
JOIN arbitro a ON u.id = a.id
WHERE u.activo = TRUE;
```

### Ver asignaciones pendientes:
```sql
SELECT 
    u.nombre AS arbitro,
    ar.escalafon,
    CONCAT(p.equipo_local, ' vs ', p.equipo_visitante) AS partido,
    p.fecha_hora,
    a.monto,
    a.estado
FROM asignacion a
JOIN arbitro ar ON a.arbitro_id = ar.id
JOIN usuario u ON ar.id = u.id
JOIN partido p ON a.partido_id = p.id
WHERE a.estado = 'PENDIENTE'
ORDER BY p.fecha_hora;
```

### Ver liquidaciones por árbitro:
```sql
SELECT 
    u.nombre AS arbitro,
    l.periodo_inicio,
    l.periodo_fin,
    l.monto_total,
    l.estado,
    l.fecha_pago
FROM liquidacion l
JOIN arbitro ar ON l.arbitro_id = ar.id
JOIN usuario u ON ar.id = u.id
ORDER BY l.fecha_generacion DESC;
```

## 🔐 Configuración de Spring Boot

Asegúrate de que `application.properties` tenga:

```properties
# Para AWS RDS
spring.datasource.url=jdbc:mysql://caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com:3306/caba_pro
spring.datasource.username=admin
spring.datasource.password=CABAPro2025!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
```

## 🛠️ Mantenimiento

### Resetear la base de datos (SOLO DESARROLLO):
```sql
DROP DATABASE IF EXISTS caba_pro;
CREATE DATABASE caba_pro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE caba_pro;
-- Luego ejecutar init-db.sql
```

### Backup:
```bash
mysqldump -h caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com -u admin -p caba_pro > backup_$(date +%Y%m%d).sql
```

### Restore:
```bash
mysql -h caba-db.xxxxxxxxxx.us-east-1.rds.amazonaws.com -u admin -p caba_pro < backup_20251110.sql
```

## 📝 Notas Importantes

1. **Contraseñas hasheadas**: Las passwords en el script están con bcrypt hash (123456)
2. **Datos de prueba**: Solo para desarrollo, no usar en producción
3. **DDL Auto**: Spring Boot creará/actualizará tablas automáticamente
4. **Character Set**: Todas las tablas usan utf8mb4 para soportar emojis y caracteres especiales
5. **Índices**: Todas las tablas tienen índices en campos frecuentemente consultados

## 🔄 Sincronización con Spring Boot

El script SQL está 100% alineado con las entidades JPA de Spring Boot:
- ✅ Nombres de tablas coinciden
- ✅ Nombres de columnas coinciden
- ✅ Tipos de datos compatibles
- ✅ Relaciones FK configuradas
- ✅ Índices optimizados

Si haces cambios en las entidades JPA, considera actualizar este script.
