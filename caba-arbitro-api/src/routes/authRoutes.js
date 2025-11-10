import express from 'express';
import { body } from 'express-validator';
import authController from '../controllers/authController.js';
import { authMiddleware } from '../middleware/authMiddleware.js';

const router = express.Router();

/**
 * @route   POST /api/auth/login
 * @desc    Login de usuario
 * @access  Public
 */
router.post('/login',
    [
        body('email').isEmail().withMessage('Email inválido'),
        body('password').notEmpty().withMessage('Password requerido')
    ],
    authController.login
);

/**
 * @route   POST /api/auth/register
 * @desc    Registro de nuevo árbitro
 * @access  Public
 */
router.post('/register',
    [
        body('nombre').notEmpty().withMessage('Nombre requerido'),
        body('apellido').notEmpty().withMessage('Apellido requerido'),
        body('email').isEmail().withMessage('Email inválido'),
        body('password').isLength({ min: 6 }).withMessage('Password debe tener al menos 6 caracteres'),
        body('numeroLicencia').notEmpty().withMessage('Número de licencia requerido'),
        body('especialidad').notEmpty().withMessage('Especialidad requerida'),
        body('escalafon').notEmpty().withMessage('Escalafón requerido')
    ],
    authController.register
);

/**
 * @route   POST /api/auth/refresh
 * @desc    Refrescar token JWT
 * @access  Private (requiere token)
 */
router.post('/refresh',
    authMiddleware,
    authController.refresh
);

/**
 * @route   POST /api/auth/logout
 * @desc    Cerrar sesión
 * @access  Private (requiere token)
 */
router.post('/logout',
    authMiddleware,
    authController.logout
);

export default router;
