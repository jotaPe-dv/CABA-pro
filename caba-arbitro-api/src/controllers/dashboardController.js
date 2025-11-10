import dashboardService from '../services/dashboardService.js';

/**
 * Controlador de dashboard
 */
class DashboardController {
    
    /**
     * Obtener dashboard completo
     * GET /api/arbitro/dashboard
     */
    async obtenerDashboard(req, res) {
        try {
            const token = req.token;
            const result = await dashboardService.obtenerDashboard(token);

            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error al obtener dashboard:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Obtener estadísticas
     * GET /api/arbitro/estadisticas
     */
    async obtenerEstadisticas(req, res) {
        try {
            const token = req.token;
            const { meses } = req.query;
            
            const result = await dashboardService.obtenerEstadisticas(
                token, 
                meses ? parseInt(meses) : 3
            );

            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error al obtener estadísticas:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }
}

export default new DashboardController();
