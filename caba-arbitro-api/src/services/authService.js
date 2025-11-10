import apiClient from '../config/api.js';

/**
 * Servicio para autenticación - consume endpoints de Spring Boot
 */
class AuthService {
    
    /**
     * Login - POST /api/auth/login
     */
    async login(email, password) {
        try {
            const response = await apiClient.post('/api/auth/login', {
                email,
                password
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Registro - POST /api/auth/register
     */
    async register(userData) {
        try {
            const response = await apiClient.post('/api/auth/register', userData);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Refrescar token - POST /api/auth/refresh
     */
    async refreshToken(token) {
        try {
            const response = await apiClient.post('/api/auth/refresh', null, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Logout - POST /api/auth/logout
     */
    async logout(token) {
        try {
            const response = await apiClient.post('/api/auth/logout', null, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Manejo centralizado de errores
     */
    handleError(error) {
        if (error.response) {
            // El servidor respondió con un código de error
            return {
                status: error.response.status,
                message: error.response.data.message || error.response.data.error || 'Error en la autenticación',
                data: error.response.data
            };
        } else if (error.request) {
            // La petición se hizo pero no hubo respuesta
            return {
                status: 503,
                message: 'Servicio no disponible. La API de Spring Boot no responde',
                data: null
            };
        } else {
            // Error al configurar la petición
            return {
                status: 500,
                message: error.message || 'Error interno del servidor',
                data: null
            };
        }
    }
}

export default new AuthService();
