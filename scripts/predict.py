"""
Script de ejemplo para hacer inferencia con el modelo entrenado.

Uso:
    python predict.py --model models/predictor.pkl --local "Equipo A" --visitante "Equipo B" --fase "Semifinal"
"""

import pickle
import numpy as np
import argparse


def cargar_modelo(model_path):
    """Carga el modelo y encoders desde archivo pickle."""
    with open(model_path, 'rb') as f:
        bundle = pickle.load(f)
    return bundle


def predecir_partido(bundle, equipo_local, equipo_visitante, fase='Regular'):
    """
    Realiza predicción de un partido.
    
    Returns:
        dict con probabilidades, ganador predicho, y margen esperado
    """
    clf = bundle['clasificador']
    reg = bundle['regressor']
    le_local = bundle['encoder_local']
    le_visitante = bundle['encoder_visitante']
    le_fase = bundle['encoder_fase']
    
    # Encodear inputs
    try:
        equipoLocal_enc = le_local.transform([equipo_local])[0]
        equipoVisitante_enc = le_visitante.transform([equipo_visitante])[0]
        fase_enc = le_fase.transform([fase])[0]
    except ValueError as e:
        return {
            'error': f'Equipo o fase desconocido: {e}',
            'info': 'El modelo solo conoce equipos y fases que aparecen en los datos de entrenamiento'
        }
    
    # Crear feature vector
    ventaja_local = 1
    num_arbitros_aceptados = 4  # Asumimos 4 árbitros estándar
    
    X = np.array([[equipoLocal_enc, equipoVisitante_enc, ventaja_local, 
                   num_arbitros_aceptados, fase_enc]])
    
    # Predicción de clasificación (probabilidades)
    proba_local_gana = clf.predict_proba(X)[0][1]
    proba_visitante_gana = 1 - proba_local_gana
    
    # Predicción de regresión (margen)
    margen_esperado = reg.predict(X)[0]
    
    # Determinar ganador
    ganador_predicho = equipo_local if proba_local_gana > 0.5 else equipo_visitante
    
    return {
        'equipo_local': equipo_local,
        'equipo_visitante': equipo_visitante,
        'fase': fase,
        'probabilidad_local': round(proba_local_gana, 3),
        'probabilidad_visitante': round(proba_visitante_gana, 3),
        'ganador_predicho': ganador_predicho,
        'margen_esperado': round(margen_esperado, 2),
        'confianza': round(max(proba_local_gana, proba_visitante_gana), 3)
    }


def main():
    parser = argparse.ArgumentParser(description='Predicción de partidos')
    parser.add_argument('--model', default='models/predictor.pkl', help='Path al modelo entrenado')
    parser.add_argument('--local', required=True, help='Nombre del equipo local')
    parser.add_argument('--visitante', required=True, help='Nombre del equipo visitante')
    parser.add_argument('--fase', default='Regular', help='Fase del torneo (Regular, Semifinal, Final)')
    args = parser.parse_args()
    
    print("Cargando modelo...")
    bundle = cargar_modelo(args.model)
    
    print("Realizando predicción...")
    resultado = predecir_partido(bundle, args.local, args.visitante, args.fase)
    
    if 'error' in resultado:
        print(f"❌ Error: {resultado['error']}")
        print(f"   {resultado['info']}")
    else:
        print("\n--- PREDICCIÓN ---")
        print(f"🏠 {resultado['equipo_local']} vs {resultado['equipo_visitante']} 🚌")
        print(f"📍 Fase: {resultado['fase']}")
        print(f"\n📊 Probabilidades:")
        print(f"   {resultado['equipo_local']}: {resultado['probabilidad_local']*100:.1f}%")
        print(f"   {resultado['equipo_visitante']}: {resultado['probabilidad_visitante']*100:.1f}%")
        print(f"\n🏆 Ganador predicho: {resultado['ganador_predicho']}")
        print(f"📈 Margen esperado: {resultado['margen_esperado']:+.2f} goles")
        print(f"💯 Confianza: {resultado['confianza']*100:.1f}%")


if __name__ == '__main__':
    main()
