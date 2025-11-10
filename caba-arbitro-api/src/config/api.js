import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

/**
 * Instancia de Axios configurada para consumir la API Spring Boot
 */
const apiClient = axios.create({
    baseURL: process.env.SPRING_API_URL || 'http://localhost:8081',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    }
});

/**
 * Interceptor para agregar el token JWT en todas las peticiones
 */
apiClient.interceptors.request.use(
    (config) => {
        // Si la petición tiene un token en los headers, lo pasamos
        const token = config.headers.Authorization;
        if (token) {
            config.headers.Authorization = token;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

/**
 * Interceptor para manejar errores de respuesta
 */
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            // El servidor respondió con un código de error
            console.error('API Error:', error.response.status, error.response.data);
        } else if (error.request) {
            // La petición se hizo pero no hubo respuesta
            console.error('No response from API:', error.message);
        } else {
            // Error al configurar la petición
            console.error('Request error:', error.message);
        }
        return Promise.reject(error);
    }
);

export default apiClient;
