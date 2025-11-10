import express from 'express';
import { body } from 'express-validator';
import arbitroController from '../controllers/arbitroController.js';
import { authMiddleware } from '../middleware/authMiddleware.js';

const router = express.Router();

// Todas las rutas requieren autenticación
router.use(authMiddleware);

/**
 * @route   GET /api/arbitro/perfil
 * @desc    Obtener perfil del árbitro autenticado
 * @access  Private
 */
router.get('/perfil', arbitroController.obtenerPerfil);

/**
 * @route   PUT /api/arbitro/perfil
 * @desc    Actualizar perfil del árbitro
 * @access  Private
 */
router.put('/perfil',
    [
        body('telefono').optional().matches(/^[0-9]{10}$/).withMessage('Teléfono debe tener 10 dígitos'),
        body('direccion').optional().isLength({ max: 100 }).withMessage('Dirección muy larga'),
        body('fotoUrl').optional().isURL().withMessage('URL de foto inválida')
    ],
    arbitroController.actualizarPerfil
);

/**
 * @route   PUT /api/arbitro/disponibilidad
 * @desc    Cambiar disponibilidad del árbitro
 * @access  Private
 */
router.put('/disponibilidad',
    [
        body('disponible').optional().isBoolean().withMessage('Disponible debe ser boolean')
    ],
    arbitroController.cambiarDisponibilidad
);

/**
 * @route   GET /api/arbitro/mis-asignaciones
 * @desc    Obtener asignaciones del árbitro (opcional: ?estado=PENDIENTE)
 * @access  Private
 */
router.get('/mis-asignaciones', arbitroController.obtenerMisAsignaciones);

/**
 * @route   POST /api/arbitro/asignacion/:id/aceptar
 * @desc    Aceptar una asignación
 * @access  Private
 */
router.post('/asignacion/:id/aceptar', arbitroController.aceptarAsignacion);

/**
 * @route   POST /api/arbitro/asignacion/:id/rechazar
 * @desc    Rechazar una asignación
 * @access  Private
 */
router.post('/asignacion/:id/rechazar',
    [
        body('comentario').optional().isString().withMessage('Comentario debe ser texto')
    ],
    arbitroController.rechazarAsignacion
);

/**
 * @route   GET /api/arbitro/mis-liquidaciones
 * @desc    Obtener liquidaciones del árbitro (opcional: ?estado=PENDIENTE)
 * @access  Private
 */
router.get('/mis-liquidaciones', arbitroController.obtenerMisLiquidaciones);

export default router;
