import apiClient from '../config/api.js';

/**
 * Servicio para árbitros - consume endpoints de Spring Boot
 */
class ArbitroService {
    
    /**
     * Obtener perfil del árbitro autenticado
     * GET /api/arbitro/perfil
     */
    async obtenerPerfil(token) {
        try {
            const response = await apiClient.get('/api/arbitro/perfil', {
                headers: { Authorization: `Bearer ${token}` }
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Actualizar perfil del árbitro
     * PUT /api/arbitro/perfil
     */
    async actualizarPerfil(token, data) {
        try {
            const response = await apiClient.put('/api/arbitro/perfil', data, {
                headers: { Authorization: `Bearer ${token}` }
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Cambiar disponibilidad
     * PUT /api/arbitro/disponibilidad
     */
    async cambiarDisponibilidad(token, disponible = null) {
        try {
            const payload = disponible !== null ? { disponible } : {};
            const response = await apiClient.put('/api/arbitro/disponibilidad', payload, {
                headers: { Authorization: `Bearer ${token}` }
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Obtener mis asignaciones
     * GET /api/arbitro/mis-asignaciones?estado=PENDIENTE
     */
    async obtenerMisAsignaciones(token, estado = null) {
        try {
            const params = estado ? { estado } : {};
            const response = await apiClient.get('/api/arbitro/mis-asignaciones', {
                headers: { Authorization: `Bearer ${token}` },
                params
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Aceptar asignación
     * POST /api/arbitro/asignacion/:id/aceptar
     */
    async aceptarAsignacion(token, asignacionId) {
        try {
            const response = await apiClient.post(
                `/api/arbitro/asignacion/${asignacionId}/aceptar`,
                {},
                { headers: { Authorization: `Bearer ${token}` } }
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Rechazar asignación
     * POST /api/arbitro/asignacion/:id/rechazar
     */
    async rechazarAsignacion(token, asignacionId, comentario = '') {
        try {
            const response = await apiClient.post(
                `/api/arbitro/asignacion/${asignacionId}/rechazar`,
                { comentario },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }

    /**
     * Obtener mis liquidaciones
     * GET /api/arbitro/mis-liquidaciones?estado=PENDIENTE
     */
    async obtenerMisLiquidaciones(token, estado = null) {
        try {
            const params = estado ? { estado } : {};
            const response = await apiClient.get('/api/arbitro/mis-liquidaciones', {
                headers: { Authorization: `Bearer ${token}` },
                params
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
                message: error.response.data.message || error.response.data.error || 'Error en la operación',
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

export default new ArbitroService();
