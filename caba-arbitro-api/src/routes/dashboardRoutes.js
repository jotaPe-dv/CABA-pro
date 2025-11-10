import express from 'express';
import dashboardController from '../controllers/dashboardController.js';
import { authMiddleware } from '../middleware/authMiddleware.js';

const router = express.Router();

// Todas las rutas requieren autenticación
router.use(authMiddleware);

/**
 * @route   GET /api/arbitro/dashboard
 * @desc    Obtener dashboard completo con estadísticas
 * @access  Private
 */
router.get('/dashboard', dashboardController.obtenerDashboard);

/**
 * @route   GET /api/arbitro/estadisticas
 * @desc    Obtener estadísticas mensuales (opcional: ?meses=3)
 * @access  Private
 */
router.get('/estadisticas', dashboardController.obtenerEstadisticas);

export default router;
