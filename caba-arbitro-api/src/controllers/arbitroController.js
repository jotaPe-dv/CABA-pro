import arbitroService from '../services/arbitroService.js';
import { validationResult } from 'express-validator';

/**
 * Controlador de árbitro
 */
class ArbitroController {
    
    /**
     * Obtener perfil
     * GET /api/arbitro/perfil
     */
    async obtenerPerfil(req, res) {
        try {
            const token = req.token;
            const result = await arbitroService.obtenerPerfil(token);

            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error al obtener perfil:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Actualizar perfil
     * PUT /api/arbitro/perfil
     */
    async actualizarPerfil(req, res) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                return res.status(400).json({
                    error: 'Datos inválidos',
                    details: errors.array()
                });
            }

            const token = req.token;
            const data = req.body;
            
            const result = await arbitroService.actualizarPerfil(token, data);

            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error al actualizar perfil:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Cambiar disponibilidad
     * PUT /api/arbitro/disponibilidad
     */
    async cambiarDisponibilidad(req, res) {
        try {
            const token = req.token;
            const { disponible } = req.body;
            
            const result = await arbitroService.cambiarDisponibilidad(token, disponible);

            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error al cambiar disponibilidad:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Obtener mis asignaciones
     * GET /api/arbitro/mis-asignaciones
     */
    async obtenerMisAsignaciones(req, res) {
        try {
            const token = req.token;
            const { estado } = req.query;
            
            const result = await arbitroService.obtenerMisAsignaciones(token, estado);

            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error al obtener asignaciones:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Aceptar asignación
     * POST /api/arbitro/asignacion/:id/aceptar
     */
    async aceptarAsignacion(req, res) {
        try {
            const token = req.token;
            const { id } = req.params;
            
            const result = await arbitroService.aceptarAsignacion(token, id);

            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error al aceptar asignación:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Rechazar asignación
     * POST /api/arbitro/asignacion/:id/rechazar
     */
    async rechazarAsignacion(req, res) {
        try {
            const token = req.token;
            const { id } = req.params;
            const { comentario } = req.body;
            
            const result = await arbitroService.rechazarAsignacion(token, id, comentario);

            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error al rechazar asignación:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Obtener mis liquidaciones
     * GET /api/arbitro/mis-liquidaciones
     */
    async obtenerMisLiquidaciones(req, res) {
        try {
            const token = req.token;
            const { estado } = req.query;
            
            const result = await arbitroService.obtenerMisLiquidaciones(token, estado);

            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error al obtener liquidaciones:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }
}

export default new ArbitroController();
