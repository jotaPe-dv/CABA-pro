import apiClient from '../config/api.js';

/**
 * Servicio para dashboard - consume endpoints de Spring Boot
 */
class DashboardService {
    
    /**
     * Obtener dashboard completo
     * GET /api/arbitro/dashboard
     */
    async obtenerDashboard(token) {
        try {
            const response = await apiClient.get('/api/arbitro/dashboard', {
                headers: { Authorization: `Bearer ${token}` }
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Obtener estadísticas mensuales
     * GET /api/arbitro/estadisticas?meses=3
     */
    async obtenerEstadisticas(token, meses = 3) {
        try {
            const response = await apiClient.get('/api/arbitro/estadisticas', {
                headers: { Authorization: `Bearer ${token}` },
                params: { meses }
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
            return {
                status: error.response.status,
                message: error.response.data.message || error.response.data.error || 'Error al obtener dashboard',
                data: error.response.data
            };
        } else if (error.request) {
            return {
                status: 503,
                message: 'Servicio no disponible',
                data: null
            };
        } else {
            return {
                status: 500,
                message: error.message || 'Error interno',
                data: null
            };
        }
    }
}

export default new DashboardService();
