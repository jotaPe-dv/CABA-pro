import express from 'express';
import cors from 'cors';
import morgan from 'morgan';
import dotenv from 'dotenv';

// Importar rutas
import authRoutes from './routes/authRoutes.js';
import arbitroRoutes from './routes/arbitroRoutes.js';
import dashboardRoutes from './routes/dashboardRoutes.js';

// Configurar variables de entorno
dotenv.config();

// Crear aplicación Express
const app = express();

// Configuración del puerto
const PORT = process.env.PORT || 3000;

// ========== MIDDLEWARES ==========

// CORS - Permitir peticiones desde frontend
app.use(cors({
    origin: ['http://localhost:3000', 'http://localhost:4200', 'http://localhost:5173'],
    credentials: true,
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));

// Body parser
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Logger HTTP
if (process.env.NODE_ENV === 'development') {
    app.use(morgan('dev'));
} else {
    app.use(morgan('combined'));
}

// ========== RUTAS ==========

// Ruta de health check
app.get('/', (req, res) => {
    res.json({
        message: 'CABA Árbitro API - Node.js/Express',
        version: '1.0.0',
        status: 'online',
        endpoints: {
            auth: '/api/auth',
            arbitro: '/api/arbitro',
            dashboard: '/api/arbitro/dashboard'
        },
        springBootAPI: process.env.SPRING_API_URL
    });
});

// Health check endpoint
app.get('/health', (req, res) => {
    res.json({
        status: 'ok',
        timestamp: new Date().toISOString(),
        uptime: process.uptime(),
        environment: process.env.NODE_ENV
    });
});

// Montar rutas de la API
app.use('/api/auth', authRoutes);
app.use('/api/arbitro', arbitroRoutes);
app.use('/api/arbitro', dashboardRoutes); // Dashboard está en /api/arbitro/dashboard

// ========== MANEJO DE ERRORES ==========

// Ruta no encontrada (404)
app.use((req, res) => {
    res.status(404).json({
        error: 'Endpoint no encontrado',
        path: req.path,
        method: req.method,
        message: 'La ruta solicitada no existe'
    });
});

// Manejo global de errores
app.use((error, req, res, next) => {
    console.error('Error global:', error);
    
    res.status(error.status || 500).json({
        error: error.message || 'Error interno del servidor',
        ...(process.env.NODE_ENV === 'development' && { stack: error.stack })
    });
});

// ========== INICIAR SERVIDOR ==========

app.listen(PORT, () => {
    console.log('='.repeat(60));
    console.log('🚀 CABA Árbitro API - Node.js/Express');
    console.log('='.repeat(60));
    console.log(`📡 Servidor corriendo en: http://localhost:${PORT}`);
    console.log(`🌍 Entorno: ${process.env.NODE_ENV || 'development'}`);
    console.log(`🔗 Spring Boot API: ${process.env.SPRING_API_URL}`);
    console.log('='.repeat(60));
    console.log('📚 Endpoints disponibles:');
    console.log('   - GET  /                              → Info de la API');
    console.log('   - GET  /health                        → Health check');
    console.log('   - POST /api/auth/login                → Login');
    console.log('   - POST /api/auth/register             → Registro');
    console.log('   - POST /api/auth/refresh              → Refrescar token');
    console.log('   - POST /api/auth/logout               → Logout');
    console.log('   - GET  /api/arbitro/perfil            → Ver perfil');
    console.log('   - PUT  /api/arbitro/perfil            → Actualizar perfil');
    console.log('   - PUT  /api/arbitro/disponibilidad    → Cambiar disponibilidad');
    console.log('   - GET  /api/arbitro/mis-asignaciones  → Ver asignaciones');
    console.log('   - POST /api/arbitro/asignacion/:id/aceptar  → Aceptar asignación');
    console.log('   - POST /api/arbitro/asignacion/:id/rechazar → Rechazar asignación');
    console.log('   - GET  /api/arbitro/mis-liquidaciones → Ver liquidaciones');
    console.log('   - GET  /api/arbitro/dashboard         → Dashboard completo');
    console.log('   - GET  /api/arbitro/estadisticas      → Estadísticas mensuales');
    console.log('='.repeat(60));
    console.log('✅ Servidor listo para recibir peticiones');
    console.log('='.repeat(60));
});

// Manejo de señales de terminación
process.on('SIGTERM', () => {
    console.log('🛑 SIGTERM recibido. Cerrando servidor...');
    process.exit(0);
});

process.on('SIGINT', () => {
    console.log('\n🛑 SIGINT recibido. Cerrando servidor...');
    process.exit(0);
});

export default app;
