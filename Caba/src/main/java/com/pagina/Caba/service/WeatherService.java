package com.pagina.Caba.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Servicio para consumir la API de OpenWeatherMap
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@Service
public class WeatherService {

    private final WebClient webClient;
    
    @Value("${openweather.api.key:demo}")
    private String apiKey;
    
    @Value("${openweather.api.url:https://api.openweathermap.org/data/2.5}")
    private String apiUrl;

    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openweathermap.org/data/2.5").build();
    }

    /**
     * Obtiene el clima actual de una ciudad
     * Los resultados se cachean por 5 minutos para evitar exceso de llamadas a la API
     * 
     * @param ciudad Nombre de la ciudad
     * @return JSON con información del clima
     */
    @Cacheable(value = "weatherCache", key = "#ciudad", unless = "#result == null")
    public WeatherData obtenerClimaPorCiudad(String ciudad) {
        try {
            String url = String.format("/weather?q=%s&appid=%s&units=metric&lang=es", 
                                     ciudad, apiKey);
            
            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response != null) {
                return parseWeatherData(response);
            }
        } catch (Exception e) {
            // Si falla la API, devolver datos por defecto
            return new WeatherData("No disponible", 0.0, "Información no disponible", "01d");
        }
        
        return new WeatherData("No disponible", 0.0, "Información no disponible", "01d");
    }

    /**
     * Obtiene el clima por coordenadas geográficas
     * 
     * @param lat Latitud
     * @param lon Longitud
     * @return JSON con información del clima
     */
    @Cacheable(value = "weatherCache", key = "#lat + ',' + #lon", unless = "#result == null")
    public WeatherData obtenerClimaPorCoordenadas(double lat, double lon) {
        try {
            String url = String.format("/weather?lat=%f&lon=%f&appid=%s&units=metric&lang=es", 
                                     lat, lon, apiKey);
            
            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response != null) {
                return parseWeatherData(response);
            }
        } catch (Exception e) {
            return new WeatherData("No disponible", 0.0, "Información no disponible", "01d");
        }
        
        return new WeatherData("No disponible", 0.0, "Información no disponible", "01d");
    }

    /**
     * Parsea la respuesta JSON de la API
     */
    private WeatherData parseWeatherData(JsonNode response) {
        String descripcion = response.path("weather").get(0).path("description").asText();
        double temperatura = response.path("main").path("temp").asDouble();
        String ciudadNombre = response.path("name").asText();
        String icono = response.path("weather").get(0).path("icon").asText();
        
        return new WeatherData(ciudadNombre, temperatura, descripcion, icono);
    }

    /**
     * Clase interna para representar datos del clima
     */
    public static class WeatherData {
        private String ciudad;
        private double temperatura;
        private String descripcion;
        private String icono;

        public WeatherData(String ciudad, double temperatura, String descripcion, String icono) {
            this.ciudad = ciudad;
            this.temperatura = temperatura;
            this.descripcion = descripcion;
            this.icono = icono;
        }

        public String getCiudad() {
            return ciudad;
        }

        public double getTemperatura() {
            return temperatura;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getIcono() {
            return icono;
        }

        public String getIconoUrl() {
            return "https://openweathermap.org/img/wn/" + icono + "@2x.png";
        }

        public String getTemperaturaFormateada() {
            return String.format("%.1f°C", temperatura);
        }
    }
}
