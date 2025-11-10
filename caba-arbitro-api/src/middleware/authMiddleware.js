import jwt from 'jsonwebtoken';
import dotenv from 'dotenv';

dotenv.config();

/**
 * Middleware para validar el token JWT
 */
export const authMiddleware = (req, res, next) => {
    try {
        // Obtener token del header Authorization
        const authHeader = req.headers.authorization;
        
        if (!authHeader) {
            return res.status(401).json({
                error: 'No se proporcionó token de autenticación',
                message: 'Token requerido en el header Authorization'
            });
        }

        // Verificar formato "Bearer <token>"
        const parts = authHeader.split(' ');
        if (parts.length !== 2 || parts[0] !== 'Bearer') {
            return res.status(401).json({
                error: 'Formato de token inválido',
                message: 'Use el formato: Bearer <token>'
            });
        }

        const token = parts[1];

        // Verificar el token JWT
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        
        // Agregar información del usuario decodificada a la request
        req.user = decoded;
        req.token = token;

        next();
    } catch (error) {
        if (error.name === 'JsonWebTokenError') {
            return res.status(401).json({
                error: 'Token inválido',
                message: 'El token proporcionado no es válido'
            });
        }
        
        if (error.name === 'TokenExpiredError') {
            return res.status(401).json({
                error: 'Token expirado',
                message: 'El token ha expirado. Por favor, inicie sesión nuevamente'
            });
        }

        return res.status(500).json({
            error: 'Error al validar token',
            message: error.message
        });
    }
};

/**
 * Middleware opcional - no retorna error si no hay token
 */
export const optionalAuth = (req, res, next) => {
    try {
        const authHeader = req.headers.authorization;
        
        if (authHeader) {
            const parts = authHeader.split(' ');
            if (parts.length === 2 && parts[0] === 'Bearer') {
                const token = parts[1];
                const decoded = jwt.verify(token, process.env.JWT_SECRET);
                req.user = decoded;
                req.token = token;
            }
        }
        
        next();
    } catch (error) {
        // Si hay error, simplemente continuamos sin usuario autenticado
        next();
    }
};
