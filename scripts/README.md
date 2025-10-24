# Scripts de Entrenamiento para Modelo de Predicción

Esta carpeta contiene scripts Python para entrenar e inferir con modelos de predicción de partidos usando los datos exportados desde la aplicación CABA-pro.

## 📋 Requisitos

Instalar dependencias:

```bash
pip install pandas scikit-learn lightgbm numpy
```

## 🚀 Flujo de Trabajo

### 1. Generar Datos de Entrenamiento (desde la API)

```bash
# Generar 100 torneos simulados con aceptación automática
curl -X POST "http://localhost:8080/api/torneos/ai/batch?numTorneos=100&autoAceptar=true"

# Exportar datos a JSONL
curl -X GET "http://localhost:8080/api/torneos/ai/export?outputPath=data/torneos_export.jsonl"
```

### 2. Entrenar el Modelo

```bash
python train_model.py --input data/torneos_export.jsonl --output models/predictor.pkl
```

Esto generará:
- **Modelo de clasificación**: Predice ganador (probabilidades)
- **Modelo de regresión**: Predice margen de goles
- **Encoders**: Para equipos y fases del torneo

### 3. Hacer Predicciones

```bash
python predict.py --model models/predictor.pkl --local "Equipo A" --visitante "Equipo B" --fase "Semifinal"
```

Salida ejemplo:
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

## 📊 Features Utilizadas

El modelo utiliza las siguientes características:
- **Equipo local** (codificado)
- **Equipo visitante** (codificado)
- **Ventaja local** (1 para local, 0 para visitante)
- **Número de árbitros aceptados** (4 estándar)
- **Fase del torneo** (Regular, Semifinal, Final)

## 🔄 Mejoras Futuras

1. **Estadísticas históricas**: Agregar win rate, promedio de goles, enfrentamientos directos
2. **Features temporales**: Racha actual, días de descanso
3. **Features de árbitros**: Experiencia, estilo de arbitraje
4. **Embeddings de equipos**: Usar técnicas de NLP para capturar similitudes
5. **Modelos avanzados**: XGBoost, redes neuronales, ensemble methods

## 📝 Notas

- El modelo solo puede predecir equipos que aparezcan en los datos de entrenamiento
- Se recomienda entrenar con al menos 50-100 torneos simulados para resultados confiables
- Los datos deben incluir `simulado: true` en metadatos para distinguir de partidos reales
