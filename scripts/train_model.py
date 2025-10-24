"""
Script de ejemplo para entrenar un modelo de predicción de partidos
usando datos exportados de la aplicación CABA-pro.

Requisitos:
    pip install pandas scikit-learn lightgbm

Uso:
    python train_model.py --input data/torneos_export.jsonl --output models/predictor.pkl
"""

import json
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import accuracy_score, mean_absolute_error, log_loss
import lightgbm as lgb
import pickle
import argparse


def cargar_datos(input_path):
    """Carga datos desde archivo JSONL."""
    registros = []
    with open(input_path, 'r', encoding='utf-8') as f:
        for line in f:
            registros.append(json.loads(line))
    return pd.DataFrame(registros)


def extraer_features(df):
    """
    Extrae features para el modelo a partir de los datos crudos.
    """
    # Encoders para equipos
    le_local = LabelEncoder()
    le_visitante = LabelEncoder()
    
    # Combinar todos los equipos para crear vocabulario común
    todos_equipos = pd.concat([df['equipoLocal'], df['equipoVisitante']]).unique()
    le_local.fit(todos_equipos)
    le_visitante.fit(todos_equipos)
    
    # Encodear equipos
    df['equipoLocal_enc'] = le_local.transform(df['equipoLocal'])
    df['equipoVisitante_enc'] = le_visitante.transform(df['equipoVisitante'])
    
    # Ventaja local (1 si es local, 0 si visitante)
    df['ventaja_local'] = 1
    
    # Features de asignaciones (número de árbitros aceptados)
    df['num_arbitros_aceptados'] = df['asignaciones'].apply(
        lambda asigs: sum(1 for a in asigs if a.get('estado') == 'ACEPTADA')
    )
    
    # Fase del torneo (encodear)
    le_fase = LabelEncoder()
    df['fase_enc'] = le_fase.fit_transform(df['fase'].fillna('Regular'))
    
    # Target: ganador (1 si ganó local, 0 si ganó visitante)
    df['ganador_es_local'] = (df['ganador'] == df['equipoLocal']).astype(int)
    
    # Margen de goles
    df['margen'] = df['marcadorLocal'] - df['marcadorVisitante']
    
    features = ['equipoLocal_enc', 'equipoVisitante_enc', 'ventaja_local', 
                'num_arbitros_aceptados', 'fase_enc']
    
    return df, features, le_local, le_visitante, le_fase


def entrenar_modelo(df, features):
    """
    Entrena un modelo LightGBM para clasificación (ganador) y regresión (margen).
    """
    X = df[features]
    y_clasificacion = df['ganador_es_local']
    y_regresion = df['margen']
    
    # Split train/test
    X_train, X_test, y_class_train, y_class_test, y_reg_train, y_reg_test = train_test_split(
        X, y_clasificacion, y_regresion, test_size=0.2, random_state=42
    )
    
    # Modelo de clasificación (probabilidad de victoria local)
    print("Entrenando modelo de clasificación...")
    clf = lgb.LGBMClassifier(n_estimators=100, learning_rate=0.05, max_depth=5, random_state=42)
    clf.fit(X_train, y_class_train)
    
    y_pred_proba = clf.predict_proba(X_test)[:, 1]
    y_pred_class = (y_pred_proba > 0.5).astype(int)
    acc = accuracy_score(y_class_test, y_pred_class)
    logloss = log_loss(y_class_test, y_pred_proba)
    
    print(f"  Accuracy: {acc:.3f}")
    print(f"  Log Loss: {logloss:.3f}")
    
    # Modelo de regresión (margen esperado)
    print("Entrenando modelo de regresión...")
    reg = lgb.LGBMRegressor(n_estimators=100, learning_rate=0.05, max_depth=5, random_state=42)
    reg.fit(X_train, y_reg_train)
    
    y_pred_margin = reg.predict(X_test)
    mae = mean_absolute_error(y_reg_test, y_pred_margin)
    
    print(f"  MAE (margen): {mae:.3f}")
    
    return clf, reg


def guardar_modelo(clf, reg, le_local, le_visitante, le_fase, output_path):
    """Guarda los modelos y encoders en un archivo pickle."""
    modelo_bundle = {
        'clasificador': clf,
        'regressor': reg,
        'encoder_local': le_local,
        'encoder_visitante': le_visitante,
        'encoder_fase': le_fase
    }
    with open(output_path, 'wb') as f:
        pickle.dump(modelo_bundle, f)
    print(f"Modelo guardado en: {output_path}")


def main():
    parser = argparse.ArgumentParser(description='Entrenar modelo de predicción de partidos')
    parser.add_argument('--input', default='data/torneos_export.jsonl', help='Path al archivo JSONL de entrada')
    parser.add_argument('--output', default='models/predictor.pkl', help='Path para guardar el modelo')
    args = parser.parse_args()
    
    print("Cargando datos...")
    df = cargar_datos(args.input)
    print(f"  Registros cargados: {len(df)}")
    
    print("Extrayendo features...")
    df, features, le_local, le_visitante, le_fase = extraer_features(df)
    print(f"  Features: {features}")
    
    clf, reg = entrenar_modelo(df, features)
    
    print("Guardando modelo...")
    guardar_modelo(clf, reg, le_local, le_visitante, le_fase, args.output)
    
    print("¡Entrenamiento completado!")


if __name__ == '__main__':
    main()
