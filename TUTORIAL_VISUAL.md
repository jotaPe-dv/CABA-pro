# 📱 Guía Visual de Uso - Sistema de Simulación

## 🎯 Resumen Rápido

El sistema permite simular torneos completos con brackets automáticos y usar los datos para entrenar modelos de IA que predicen resultados.

## 🔑 Credenciales Rápidas

```
ADMIN:
Email: admin@caba.com
Password: admin123

ÁRBITROS:
principal@caba.com / 123456
asistente@caba.com / 123456
mesa@caba.com / 123456
arbitro@caba.com / admin123
```

## 🎮 Flujo Visual Paso a Paso

```
┌─────────────────────────────────────────────────────────────────┐
│ PASO 1: INICIAR SESIÓN COMO ADMIN                               │
│                                                                  │
│  1. Abrir http://localhost:8080                                 │
│  2. Email: admin@caba.com                                       │
│  3. Password: admin123                                          │
│  4. Hacer clic en "Login"                                       │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ PASO 2: CREAR O SELECCIONAR TORNEO                              │
│                                                                  │
│  Opción A: Crear nuevo torneo                                   │
│    → Clic en "Nuevo Torneo"                                     │
│    → Nombre: "Torneo Prueba 1"                                  │
│    → Guardar                                                    │
│                                                                  │
│  Opción B: Usar torneo existente                                │
│    → Seleccionar de la lista                                    │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ PASO 3: GENERAR BRACKET (ESTRUCTURA DEL TORNEO)                 │
│                                                                  │
│  1. Hacer clic en "Generar Bracket" del torneo                  │
│  2. Ingresar 4 equipos:                                         │
│     • Equipo 1: Boca Juniors                                    │
│     • Equipo 2: River Plate                                     │
│     • Equipo 3: Racing Club                                     │
│     • Equipo 4: Independiente                                   │
│  3. Clic en "Generar"                                           │
│                                                                  │
│  ✅ RESULTADO:                                                  │
│     • 2 Semifinales creadas                                     │
│     • 1 Final creada                                            │
│     • 12 asignaciones de árbitros (4 por partido)               │
│     • Estado: PENDIENTE (esperando aceptación)                  │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ PASO 4: ACEPTAR ASIGNACIONES DE ÁRBITROS                        │
│                                                                  │
│  MÉTODO RÁPIDO (Recomendado para pruebas):                      │
│  ════════════════════════════════════════                        │
│  1. En el panel de admin, buscar el torneo                      │
│  2. Ir a la página de asignaciones                              │
│  3. Cambiar manualmente los estados a ACEPTADA                  │
│                                                                  │
│  MÉTODO MANUAL (Realista):                                      │
│  ════════════════════════════════════════════                    │
│  1. Cerrar sesión del admin                                     │
│  2. Login como: principal@caba.com / 123456                     │
│  3. Ver "Mis Asignaciones"                                      │
│  4. Aceptar todas las asignaciones pendientes                   │
│  5. Cerrar sesión                                               │
│  6. Repetir para: asistente@caba.com, mesa@caba.com, etc.      │
│  7. Login nuevamente como admin                                 │
│                                                                  │
│  ⚠️  IMPORTANTE: NO SE PUEDE SIMULAR SI HAY ASIGNACIONES        │
│      PENDIENTES O RECHAZADAS                                    │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ PASO 5: SIMULAR PARTIDOS UNO POR UNO                            │
│                                                                  │
│  SEMIFINAL 1:                                                    │
│  ───────────                                                     │
│  1. Clic en "Simular Siguiente Partido"                         │
│  2. Sistema verifica: ✅ Todas las asignaciones ACEPTADAS       │
│  3. Sistema genera marcador aleatorio (0-5 goles)               │
│  4. Resultado: Boca 3 - 1 River                                 │
│  5. Ganador: Boca Juniors → Pasa a Final                        │
│                                                                  │
│  SEMIFINAL 2:                                                    │
│  ───────────                                                     │
│  1. Clic en "Simular Siguiente Partido" nuevamente              │
│  2. Resultado: Racing 2 - 2 Independiente (define penales)      │
│  3. Ganador: Racing → Pasa a Final                              │
│                                                                  │
│  FINAL:                                                          │
│  ──────                                                          │
│  1. Clic en "Simular Siguiente Partido" (última vez)            │
│  2. Partido: Boca vs Racing                                     │
│  3. Resultado: Boca 2 - 0 Racing                                │
│  4. 🏆 CAMPEÓN: Boca Juniors                                    │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ PASO 6: VER RESULTADOS                                          │
│                                                                  │
│  1. Clic en "Ver Partidos"                                      │
│  2. Verás la lista completa:                                    │
│                                                                  │
│     ID  | Equipos              | Marcador | Fase      | Estado  │
│     ────┼──────────────────────┼──────────┼───────────┼─────────│
│     1   | Boca vs River        | 3-1      | Semifinal | ✅ COMP │
│     2   | Racing vs Indep.     | 2-2*     | Semifinal | ✅ COMP │
│     3   | Boca vs Racing       | 2-0      | Final     | ✅ COMP │
│                                                                  │
│  * Ganador por penales                                          │
└─────────────────────────────────────────────────────────────────┘
```

## 🤖 Funcionalidades de IA (Panel Inferior)

### 1. Generar Dataset (Para Entrenamiento)

```
┌─────────────────────────────────────────────────┐
│ 📊 Generar Dataset                              │
│                                                 │
│  Cantidad: [10] ←── Cambia aquí (1-1000)       │
│  [Generar Torneos]                              │
│                                                 │
│  Esto creará:                                   │
│  • 10 torneos completos                         │
│  • 30 partidos simulados automáticamente        │
│  • Asignaciones auto-aceptadas                  │
│  • Datos listos para IA                         │
└─────────────────────────────────────────────────┘

⏱️ Tiempo estimado:
- 10 torneos: ~30 segundos
- 50 torneos: ~2 minutos
- 100 torneos: ~4 minutos
```

### 2. Exportar Datos (Para Python/ML)

```
┌─────────────────────────────────────────────────┐
│ 📁 Exportar a JSONL                             │
│                                                 │
│  Ruta: C:/datos/torneos.jsonl                   │
│  [Exportar]                                     │
│                                                 │
│  Formato del archivo:                           │
│  {"torneoId":1,"partidoId":5,"equipoLocal":...} │
│  {"torneoId":1,"partidoId":6,"equipoLocal":...} │
│  {"torneoId":2,"partidoId":7,"equipoLocal":...} │
│                                                 │
│  Usar con:                                      │
│  python scripts/train_model.py --input ...      │
└─────────────────────────────────────────────────┘
```

### 3. Predecir Partido (IA Baseline)

```
┌─────────────────────────────────────────────────┐
│ 🔮 Predecir Partido                             │
│                                                 │
│  Equipo Local: [Boca Juniors    ]              │
│  Equipo Visitante: [River Plate ]              │
│  Fase: [Semifinal ▼]                            │
│  [Predecir]                                     │
│                                                 │
│  RESULTADO:                                     │
│  ┌─────────────────────────────────────────┐   │
│  │ Probabilidades:                         │   │
│  │ • Boca Juniors: 68.5%                   │   │
│  │ • River Plate: 31.5%                    │   │
│  │                                         │   │
│  │ Marcador esperado: 2.4 - 1.1           │   │
│  │ Ganador predicho: Boca Juniors          │   │
│  │ Margen: +1.3 goles                      │   │
│  └─────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

### 4. Predecir Campeón (Monte Carlo)

```
┌─────────────────────────────────────────────────┐
│ 👑 Predecir Campeón del Torneo                  │
│                                                 │
│  ID Torneo: [1    ]                             │
│  Simulaciones: [1000] ←── Más = mejor precisión│
│  [Predecir]                                     │
│                                                 │
│  RESULTADO (después de 1000 simulaciones):      │
│  ┌─────────────────────────────────────────┐   │
│  │ Favorito: Boca Juniors                  │   │
│  │ Probabilidad: 42%                       │   │
│  │                                         │   │
│  │ Distribución:                           │   │
│  │ Boca Juniors   ████████████░░░ 42%     │   │
│  │ River Plate    ████████░░░░░░ 31%      │   │
│  │ Racing         ████░░░░░░░░░░ 18%      │   │
│  │ Independiente  ██░░░░░░░░░░░░  9%      │   │
│  └─────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

## 🎬 Casos de Uso Prácticos

### Caso A: "Solo quiero probar la simulación" (2 min)

```bash
1. Login admin
2. Crear torneo "Test Rápido"
3. Generar bracket con equipos
4. Cambiar asignaciones a ACEPTADA manualmente en BD
5. Simular 3 partidos (2 semis + 1 final)
6. Ver resultados
```

### Caso B: "Quiero entrenar un modelo de IA" (10 min)

```bash
1. Login admin
2. Panel IA → Generar Dataset → Cantidad: 100
3. Esperar ~4 minutos
4. Panel IA → Exportar → Guardar en C:/datos/torneos.jsonl
5. Abrir terminal:
   python scripts/train_model.py --input C:/datos/torneos.jsonl
6. Modelo guardado en models/predictor.pkl
```

### Caso C: "Quiero predecir un Superclásico" (1 min)

```bash
1. Login admin
2. Panel IA → Predecir Partido
3. Local: Boca Juniors
4. Visitante: River Plate
5. Fase: Final
6. Clic en Predecir
7. Ver probabilidades y marcador esperado
```

## ⚠️ Errores Comunes y Soluciones

### Error: "No se puede simular partido"

```
❌ Mensaje: "Existen asignaciones pendientes o rechazadas"

✅ Solución:
   1. Ir a página de asignaciones del torneo
   2. Verificar que TODAS estén en estado ACEPTADA
   3. Si hay PENDIENTES, aceptarlas
   4. Si hay RECHAZADAS, reasignar otro árbitro
```

### Error: "No hay partidos para simular"

```
❌ Mensaje: "No se encontraron partidos pendientes"

✅ Solución:
   - Ya simulaste todos los partidos del bracket
   - Crea un nuevo torneo para seguir probando
```

### Error: "Equipo no encontrado"

```
❌ Mensaje: "No se puede crear el siguiente partido"

✅ Solución:
   - Simula las semifinales ANTES de la final
   - Los equipos de la final se determinan automáticamente
```

## 📊 Interpretación de Resultados

### Probabilidades

- **> 70%**: Victoria muy probable
- **60-70%**: Victoria probable
- **50-60%**: Partido parejo con ligera ventaja
- **40-50%**: Partido muy parejo

### Margen de Goles

- **+3 o más**: Victoria amplia
- **+1 a +2**: Victoria ajustada
- **0 a +1**: Empate o victoria mínima

### Confianza del Modelo

- **> 80%**: Alta confianza
- **60-80%**: Confianza media
- **< 60%**: Baja confianza (partido impredecible)

## 🎓 Tutorial en Video (Texto)

```
MINUTO 0:00 - Introducción
├─ ¿Qué es el sistema de simulación?
└─ Objetivos del tutorial

MINUTO 0:30 - Login y Navegación
├─ Abrir aplicación
├─ Credenciales de admin
└─ Explorar el menú

MINUTO 1:30 - Crear Torneo
├─ Botón "Nuevo Torneo"
├─ Ingresar nombre
└─ Guardar

MINUTO 2:30 - Generar Bracket
├─ Botón "Generar Bracket"
├─ Ingresar 4 equipos
├─ Ver partidos creados
└─ Ver asignaciones

MINUTO 4:00 - Aceptar Asignaciones
├─ Login como árbitro
├─ Ver asignaciones pendientes
├─ Aceptar todas
└─ Volver a admin

MINUTO 6:00 - Simular Partidos
├─ Botón "Simular Siguiente"
├─ Ver resultado semifinal 1
├─ Simular semifinal 2
├─ Simular final
└─ Ver campeón

MINUTO 8:00 - Funciones de IA
├─ Generar dataset masivo
├─ Exportar a JSONL
├─ Predecir partido
└─ Predecir campeón

MINUTO 10:00 - Entrenar Modelo Python
├─ Abrir terminal
├─ Ejecutar train_model.py
├─ Ver métricas
└─ Usar modelo entrenado

MINUTO 12:00 - Conclusión
└─ Resumen y próximos pasos
```

## 🚀 Próximos Pasos

Después de dominar lo básico:

1. **Explorar el código**: Lee `TorneoSimulationService.java`
2. **Modificar parámetros**: Cambia el rango de goles (0-5 → 0-10)
3. **Añadir features**: Agrega estadísticas de equipos
4. **Mejorar IA**: Integra modelos de Python entrenados
5. **Dashboard**: Crea visualizaciones con Chart.js

---

**¿Dudas?** Revisa `GUIA_USO_SIMULACION.md` para más detalles o `docs/AI_PIPELINE.md` para arquitectura completa.
