# 🤖 Pipeline de IA para Predicción de Torneos

## 📊 Resumen del Sistema

El sistema implementa un pipeline completo de Machine Learning para predecir resultados de partidos y ganadores de torneos, sin modificar la estructura de base de datos existente.

## 🏗️ Arquitectura

### Componentes Java (Backend)

#### 1. **TorneoSimulationService** 
Genera brackets de torneos y simula partidos secuencialmente.

**Métodos principales:**
- `generarBracket(Long torneoId, List<String> equipos)` - Crea 2 semifinales + 1 final
- `simularSiguientePartido(Long torneoId)` - Simula el siguiente partido pendiente
- `asignarArbitrosAlPartido(Partido partido)` - Asigna 4 árbitros por partido

**Reglas de negocio:**
- Solo simula si **todas** las asignaciones están en estado `ACEPTADA`
- Genera marcadores aleatorios (0-5 goles por equipo)
- Marca partidos como completados automáticamente

#### 2. **TorneoDataExportService**
Exporta datos de partidos a formato JSONL para entrenamiento ML.

**Métodos principales:**
- `exportarPartidosCompletados(String outputPath)` - Exporta todos los partidos completados
- `exportarPartidosDeTorneo(Long torneoId, String outputPath)` - Exporta un torneo específico
- `obtenerEstadisticasExportacion()` - Estadísticas de datos disponibles

**Formato JSONL:**
```json
{
  "torneoId": 1,
  "partidoId": 5,
  "fechaPartido": "2025-01-15T18:30:00",
  "equipoLocal": "Equipo A",
  "equipoVisitante": "Equipo B",
  "marcadorLocal": 3,
  "marcadorVisitante": 1,
  "ganador": "Equipo A",
  "fase": "Semifinal",
  "asignaciones": [
    {"arbitroId": 10, "rol": "PRINCIPAL", "estado": "ACEPTADA"},
    {"arbitroId": 12, "rol": "ASISTENTE_1", "estado": "ACEPTADA"}
  ],
  "metadatos": {"simulado": true}
}
```

#### 3. **TorneoBatchRunnerService**
Genera múltiples torneos automáticamente para crear datasets de entrenamiento.

**Método principal:**
- `generarTorneosAutomaticos(int numTorneos, boolean autoAceptarAsignaciones)`

**Proceso:**
1. Crea N torneos con nombres secuenciales ("Torneo Simulado 1", "Torneo Simulado 2", ...)
2. Genera bracket con 4 equipos por torneo
3. Si `autoAceptarAsignaciones=true`, acepta todas las asignaciones automáticamente
4. Simula todos los partidos (semifinales → final)
5. Retorna resumen: IDs de torneos creados y total de partidos simulados

**Uso típico:**
```bash
# Generar 100 torneos para entrenamiento
curl -X POST "http://localhost:8080/api/torneos/ai/batch?numTorneos=100&autoAceptar=true"
```

#### 4. **TorneoPredictionService**
Modelo baseline de predicción heurística + simulación Monte Carlo.

**Métodos principales:**

**a) `predecirPartido(String equipoLocal, String equipoVisitante, String fase)`**
- Analiza últimos 5 partidos de cada equipo
- Calcula tasas de victoria y promedio de goles
- Aplica ventaja local (+15%)
- Fórmula: `P(local) = 0.5 + 0.15 + 0.3 * (winRate_local - winRate_visitante)`
- Normaliza probabilidades a rango [0.1, 0.9]

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

**b) `predecirCampeon(Long torneoId, int numSimulaciones)`**
- Simulación Monte Carlo (default 1000 corridas)
- Simula el bracket completo N veces usando probabilidades estocásticas
- Cuenta victorias por equipo
- Retorna distribución de probabilidades

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

#### 5. **TorneoAIController**
API REST unificada para todas las funcionalidades de IA.

**Endpoints:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/torneos/ai/batch?numTorneos=N&autoAceptar=true` | Genera N torneos automáticamente |
| GET | `/api/torneos/ai/export?outputPath=data/export.jsonl` | Exporta todos los partidos a JSONL |
| GET | `/api/torneos/ai/export/{torneoId}?outputPath=path` | Exporta un torneo específico |
| GET | `/api/torneos/ai/stats` | Estadísticas de datos disponibles |
| POST | `/api/torneos/ai/predict` | Predice resultado de un partido |
| GET | `/api/torneos/ai/predict-champion/{torneoId}?simulations=1000` | Predice ganador del torneo |

### Componentes Python (ML Training)

#### 1. **train_model.py**
Script de entrenamiento con LightGBM.

**Features utilizadas:**
- Equipo local (codificado)
- Equipo visitante (codificado)
- Ventaja local (binaria)
- Número de árbitros aceptados
- Fase del torneo (codificado)

**Modelos entrenados:**
- **Clasificador**: Predice probabilidad de victoria local (LightGBM Classifier)
- **Regresor**: Predice margen de goles (LightGBM Regressor)

**Uso:**
```bash
python train_model.py --input data/torneos_export.jsonl --output models/predictor.pkl
```

#### 2. **predict.py**
Script de inferencia con modelo entrenado.

**Uso:**
```bash
python predict.py --model models/predictor.pkl --local "Equipo A" --visitante "Equipo B" --fase "Semifinal"
```

**Salida:**
```
--- PREDICCIÓN ---
🏠 Equipo A vs Equipo B 🚌
📍 Fase: Semifinal

📊 Probabilidades:
   Equipo A: 65.3%
   Equipo B: 34.7%

🏆 Ganador predicho: Equipo A
📈 Margen esperado: +1.20 goles
💯 Confianza: 65.3%
```

## 🔄 Flujo de Trabajo Completo

```
┌─────────────────────────────────────────────────────────────┐
│ 1. GENERACIÓN DE DATOS                                      │
│    POST /api/torneos/ai/batch?numTorneos=100&autoAceptar=true│
│    ↓                                                          │
│    • Crea 100 torneos con 4 equipos cada uno                 │
│    • Auto-acepta asignaciones de árbitros                     │
│    • Simula 300 partidos (2 semifinales + 1 final × 100)     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. EXPORTACIÓN                                               │
│    GET /api/torneos/ai/export?outputPath=data/export.jsonl   │
│    ↓                                                          │
│    • Genera archivo JSONL con 300 registros                   │
│    • Incluye: equipos, marcadores, ganador, árbitros, fase    │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. ENTRENAMIENTO (Python)                                    │
│    python train_model.py --input data/export.jsonl           │
│    ↓                                                          │
│    • Extrae features (equipos, ventaja local, fase)           │
│    • Entrena LightGBM (clasificador + regresor)              │
│    • Guarda modelo en models/predictor.pkl                    │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. PREDICCIÓN                                                │
│    Opción A: Modelo baseline (Java)                          │
│    POST /api/torneos/ai/predict                              │
│    {equipoLocal, equipoVisitante, fase}                      │
│                                                               │
│    Opción B: Modelo entrenado (Python)                       │
│    python predict.py --local "A" --visitante "B"             │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. SIMULACIÓN MONTE CARLO                                    │
│    GET /api/torneos/ai/predict-champion/1?simulations=10000  │
│    ↓                                                          │
│    • Simula torneo completo 10,000 veces                      │
│    • Retorna probabilidad de campeonato por equipo            │
└─────────────────────────────────────────────────────────────┘
```

## 🎯 Casos de Uso

### 1. Generar Dataset de Entrenamiento
```bash
# Paso 1: Generar 200 torneos
curl -X POST "http://localhost:8080/api/torneos/ai/batch?numTorneos=200&autoAceptar=true"

# Paso 2: Exportar a JSONL
curl -X GET "http://localhost:8080/api/torneos/ai/export?outputPath=C:/datos/entrenamiento.jsonl"

# Paso 3: Verificar estadísticas
curl -X GET "http://localhost:8080/api/torneos/ai/stats"
```

### 2. Entrenar Modelo ML
```bash
# Instalar dependencias
pip install pandas scikit-learn lightgbm

# Entrenar
python scripts/train_model.py --input C:/datos/entrenamiento.jsonl --output models/predictor_v1.pkl
```

### 3. Predecir Partido Individual
```bash
# Con modelo baseline (Java)
curl -X POST "http://localhost:8080/api/torneos/ai/predict" \
  -H "Content-Type: application/json" \
  -d '{
    "equipoLocal": "Boca Juniors",
    "equipoVisitante": "River Plate",
    "fase": "Final"
  }'

# Con modelo entrenado (Python)
python scripts/predict.py --model models/predictor_v1.pkl \
  --local "Boca Juniors" --visitante "River Plate" --fase "Final"
```

### 4. Predecir Ganador de Torneo
```bash
# Monte Carlo con 5000 simulaciones
curl -X GET "http://localhost:8080/api/torneos/ai/predict-champion/42?simulations=5000"
```

## 📈 Mejoras Futuras

### Corto Plazo
- [ ] Agregar features de estadísticas históricas por equipo
- [ ] Implementar cache de predicciones frecuentes
- [ ] Agregar validación de equipos antes de predicción

### Mediano Plazo
- [ ] Integrar modelo Python entrenado en backend Java (PMML, ONNX)
- [ ] Features de árbitros (experiencia, estilo)
- [ ] Embeddings de equipos para capturar similitudes

### Largo Plazo
- [ ] Modelos avanzados (XGBoost, redes neuronales)
- [ ] Sistema de reentrenamiento automático
- [ ] Dashboard de métricas de modelo en producción
- [ ] A/B testing entre modelo baseline y entrenado

## 🔒 Consideraciones de Seguridad

- Los endpoints de IA están protegidos por Spring Security
- Solo usuarios con rol `ADMIN` pueden generar torneos batch
- La exportación de datos requiere autenticación
- Los archivos JSONL deben guardarse en paths seguros

## 📝 Notas Técnicas

1. **Sin modificación de esquema**: Todo se implementó sin alterar las entidades JPA existentes
2. **Simulación secuencial**: Los partidos se simulan uno a la vez respetando el flujo del bracket
3. **Validación de asignaciones**: La simulación solo procede si todos los árbitros aceptaron
4. **Modelo baseline operacional**: No requiere entrenamiento previo, funciona inmediatamente
5. **Escalabilidad**: El batch runner puede generar miles de torneos para datasets grandes
6. **Formato estándar**: JSONL es compatible con pandas, scikit-learn, PyTorch, TensorFlow

## 📚 Referencias

- Documentación Spring Boot: https://spring.io/projects/spring-boot
- LightGBM: https://lightgbm.readthedocs.io/
- Simulación Monte Carlo: https://en.wikipedia.org/wiki/Monte_Carlo_method
