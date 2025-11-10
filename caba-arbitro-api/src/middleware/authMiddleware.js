/**
 * Middleware de autenticación para Node.js API
 * 
 * IMPORTANTE: Este middleware NO valida el token JWT.
 * Solo extrae el token y lo pasa a Spring Boot.
 * La validación real del token la hace Spring Boot.
 */

/**
 * Middleware para extraer y pasar el token JWT
 * NO valida el token localmente - eso lo hace Spring Boot
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

        // NO validamos el token aquí
        // Solo lo guardamos para pasarlo a Spring Boot en las peticiones
        req.token = token;

        next();
    } catch (error) {
        return res.status(500).json({
            error: 'Error en el middleware de autenticación',
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
                req.token = token;
            }
        }
        
        next();
    } catch (error) {
        // Si hay error, simplemente continuamos sin usuario autenticado
        next();
    }
};
