# 🎮 Guía Completa de Uso - Sistema de Simulación de Torneos

## 🔐 Credenciales de Acceso

### Administrador
```
Email: admin@caba.com
Password: admin123
```

### Árbitros para Pruebas
```
Email: principal@caba.com
Password: 123456
Rol: Principal

Email: asistente@caba.com
Password: 123456
Rol: Auxiliar

Email: mesa@caba.com
Password: 123456
Rol: Mesa

Email: arbitro@caba.com
Password: admin123
Rol: General
```

## 📋 Flujo Completo de Simulación

### Paso 1: Crear un Torneo
1. Inicia sesión como **admin@caba.com** / **admin123**
2. Ve a la sección **Torneos**
3. Crea un nuevo torneo con nombre (ej: "Torneo Prueba")
4. Guarda el ID del torneo creado

### Paso 2: Generar Bracket de Torneo
1. En la página de **Torneos**, busca el torneo que creaste
2. Haz clic en **"Generar Bracket"**
3. Ingresa los 4 equipos participantes:
   - Equipo A
   - Equipo B
   - Equipo C
   - Equipo D
4. El sistema creará automáticamente:
   - **2 Semifinales**: (A vs B) y (C vs D)
   - **1 Final**: (Ganador SF1 vs Ganador SF2)
   - **4 asignaciones de árbitros por partido** (Principal, Asistente 1, Asistente 2, Mesa)
5. Todas las asignaciones empiezan en estado **PENDIENTE**

### Paso 3: Aceptar Asignaciones de Árbitros

**IMPORTANTE:** Los partidos **NO se pueden simular** hasta que TODAS las asignaciones estén ACEPTADAS.

#### Opción A: Aceptar Manualmente (Realista)
1. Cierra sesión del admin
2. Inicia sesión como cada árbitro y acepta las asignaciones:
   - Login como **principal@caba.com** / **123456** → Aceptar asignaciones
   - Login como **asistente@caba.com** / **123456** → Aceptar asignaciones
   - Login como **mesa@caba.com** / **123456** → Aceptar asignaciones
   - Login como **arbitro@caba.com** / **admin123** → Aceptar asignaciones

#### Opción B: Aceptar Automáticamente (Pruebas Rápidas)
1. En el panel de admin, usa el botón **"Auto-aceptar Asignaciones"**
2. Esto cambiará todas las asignaciones PENDIENTES a ACEPTADAS

### Paso 4: Simular Partidos Uno a Uno
1. Haz clic en **"Simular Siguiente Partido"**
2. El sistema:
   - Verifica que todas las asignaciones estén ACEPTADAS
   - Simula el partido (genera marcadores aleatorios 0-5 goles)
   - Determina el ganador
   - Marca el partido como COMPLETADO
3. **Simulación de Semifinal 1**: Se simula el primer partido (A vs B)
4. **Simulación de Semifinal 2**: Se simula el segundo partido (C vs D)
5. **Simulación Final**: Se simula el partido final con los dos ganadores

### Paso 5: Ver Resultados
- Los resultados aparecen en la página de **Partidos**
- Cada partido muestra:
  - Equipos participantes
  - Marcador final
  - Ganador
  - Fase (Semifinal/Final)
  - Estado (COMPLETADO)

## 🤖 Funcionalidades de IA

### Generar Dataset de Entrenamiento

#### Generar Múltiples Torneos Automáticamente
```bash
# Generar 100 torneos con simulación automática
curl -X POST "http://localhost:8080/api/torneos/ai/batch?numTorneos=100&autoAceptar=true"
```

Esto creará:
- 100 torneos completos
- 300 partidos simulados (3 por torneo)
- Asignaciones auto-aceptadas
- Ideal para entrenamiento de IA

#### Exportar Datos a JSONL
```bash
# Exportar todos los partidos completados
curl -X GET "http://localhost:8080/api/torneos/ai/export?outputPath=C:/datos/torneos.jsonl"

# Exportar un torneo específico
curl -X GET "http://localhost:8080/api/torneos/ai/export/1?outputPath=C:/datos/torneo1.jsonl"
```

### Hacer Predicciones

#### Predecir Resultado de un Partido
```bash
curl -X POST "http://localhost:8080/api/torneos/ai/predict" \
  -H "Content-Type: application/json" \
  -d '{
    "equipoLocal": "Equipo A",
    "equipoVisitante": "Equipo B",
    "fase": "Semifinal"
  }'
```

**Respuesta:**
```json
{
  "p_local": 0.65,
  "p_visitante": 0.35,
  "expected_score_local": 2.3,
  "expected_score_visitante": 1.1,
  "predicted_winner": "Equipo A",
  "predicted_margin": 1.2,
  "modelo": "baseline_heuristic"
}
```

#### Predecir Ganador del Torneo (Monte Carlo)
```bash
# Simular el torneo 5000 veces
curl -X GET "http://localhost:8080/api/torneos/ai/predict-champion/1?simulations=5000"
```

**Respuesta:**
```json
{
  "probabilidades": {
    "Equipo A": 0.42,
    "Equipo B": 0.31,
    "Equipo C": 0.18,
    "Equipo D": 0.09
  },
  "favoritoPrediccion": "Equipo A",
  "probabilidadFavorito": 0.42,
  "modelo": "monte_carlo_baseline"
}
```

## 🧪 Casos de Prueba

### Caso 1: Simulación Manual Completa (5 min)
```
1. Login admin → Crear torneo "Test 1"
2. Generar bracket con 4 equipos
3. Login como cada árbitro y aceptar asignaciones
4. Login admin → Simular semifinal 1
5. Login admin → Simular semifinal 2
6. Login admin → Simular final
7. Verificar resultados en página de partidos
```

### Caso 2: Simulación Automática Rápida (1 min)
```
1. Login admin → Crear torneo "Test 2"
2. Generar bracket con 4 equipos
3. Hacer clic en "Auto-aceptar Asignaciones"
4. Simular semifinal 1
5. Simular semifinal 2
6. Simular final
```

### Caso 3: Error de Asignaciones No Aceptadas
```
1. Login admin → Crear torneo "Test 3"
2. Generar bracket
3. Intentar simular sin aceptar asignaciones
4. ❌ Debe mostrar error: "No se puede simular: asignaciones pendientes"
5. Aceptar asignaciones
6. ✅ Ahora sí se puede simular
```

### Caso 4: Generación Masiva para IA (5 min)
```
1. POST /api/torneos/ai/batch?numTorneos=50&autoAceptar=true
2. Esperar a que termine (50 torneos = 150 partidos)
3. GET /api/torneos/ai/stats → Verificar que hay 150 partidos completados
4. GET /api/torneos/ai/export?outputPath=data/test.jsonl
5. Verificar archivo JSONL creado con 150 líneas
```

## 📊 Verificación de Estados

### Estados de Asignación
- **PENDIENTE**: Árbitro aún no ha respondido
- **ACEPTADA**: Árbitro aceptó (requerido para simular)
- **RECHAZADA**: Árbitro rechazó (debe reasignar)
- **COMPLETADA**: Partido ya se simuló

### Estados de Partido
- **Nuevo**: Partido creado, asignaciones pendientes
- **Listo**: Todas las asignaciones aceptadas, puede simularse
- **COMPLETADO**: Partido ya simulado con marcador final

## 🔧 Solución de Problemas

### Problema: "No se puede simular partido"
**Solución:** Verifica que todas las asignaciones estén en estado ACEPTADA

### Problema: "No hay más partidos para simular"
**Solución:** Ya se simularon todos los partidos del bracket (2 semifinales + 1 final)

### Problema: "Equipo no encontrado para siguiente fase"
**Solución:** Simula los partidos en orden: semifinales primero, final después

### Problema: Error 403 en API
**Solución:** Debes estar autenticado. Usa las credenciales de admin para endpoints REST.

## 🎯 Endpoints REST Disponibles

### Simulación
```
POST /api/torneos/simulacion/generar/{torneoId}
  Body: ["Equipo A", "Equipo B", "Equipo C", "Equipo D"]

POST /api/torneos/simulacion/simular-siguiente/{torneoId}
```

### IA (Requiere autenticación admin)
```
POST /api/torneos/ai/batch?numTorneos=N&autoAceptar=true
GET  /api/torneos/ai/export?outputPath=ruta.jsonl
GET  /api/torneos/ai/export/{torneoId}?outputPath=ruta.jsonl
GET  /api/torneos/ai/stats
POST /api/torneos/ai/predict
GET  /api/torneos/ai/predict-champion/{torneoId}?simulations=1000
```

## 📈 Métricas de Éxito

Una simulación exitosa debe:
- ✅ Crear 3 partidos (2 semifinales + 1 final)
- ✅ Asignar 12 árbitros en total (4 por partido)
- ✅ Todas las asignaciones en estado ACEPTADA antes de simular
- ✅ Generar marcadores válidos (0-5 goles por equipo)
- ✅ Determinar un ganador claro del torneo
- ✅ Marcar todos los partidos como COMPLETADO

---

**¿Necesitas ayuda?** Revisa la documentación completa en `docs/AI_PIPELINE.md` o los scripts Python en `scripts/README.md`
