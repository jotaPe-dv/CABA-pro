import authService from '../services/authService.js';
import { validationResult } from 'express-validator';

/**
 * Controlador de autenticación
 */
class AuthController {
    
    /**
     * Login
     * POST /api/auth/login
     */
    async login(req, res) {
        try {
            // Validar datos de entrada
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                return res.status(400).json({
                    error: 'Datos inválidos',
                    details: errors.array()
                });
            }

            const { email, password } = req.body;

            // Llamar al servicio de autenticación
            const result = await authService.login(email, password);

            // Si hay error en el servicio
            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            // Login exitoso
            return res.status(200).json(result);

        } catch (error) {
            console.error('Error en login:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Registro
     * POST /api/auth/register
     */
    async register(req, res) {
        try {
            // Validar datos de entrada
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                return res.status(400).json({
                    error: 'Datos inválidos',
                    details: errors.array()
                });
            }

            const userData = req.body;

            // Llamar al servicio de registro
            const result = await authService.register(userData);

            // Si hay error en el servicio
            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            // Registro exitoso
            return res.status(201).json(result);

        } catch (error) {
            console.error('Error en registro:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Refrescar token
     * POST /api/auth/refresh
     */
    async refresh(req, res) {
        try {
            const token = req.token; // Obtener del middleware

            if (!token) {
                return res.status(401).json({
                    error: 'Token no proporcionado'
                });
            }

            // Llamar al servicio de refresh
            const result = await authService.refreshToken(token);

            // Si hay error en el servicio
            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error en refresh:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }

    /**
     * Logout
     * POST /api/auth/logout
     */
    async logout(req, res) {
        try {
            const token = req.token; // Obtener del middleware

            if (!token) {
                return res.status(401).json({
                    error: 'Token no proporcionado'
                });
            }

            // Llamar al servicio de logout
            const result = await authService.logout(token);

            // Si hay error en el servicio
            if (result.status && result.status >= 400) {
                return res.status(result.status).json({
                    error: result.message,
                    data: result.data
                });
            }

            return res.status(200).json(result);

        } catch (error) {
            console.error('Error en logout:', error);
            return res.status(500).json({
                error: 'Error interno del servidor',
                message: error.message
            });
        }
    }
}

export default new AuthController();
