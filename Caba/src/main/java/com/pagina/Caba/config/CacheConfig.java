package com.pagina.Caba.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de caché para la aplicación
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configura el gestor de caché con TTL de 5 minutos para el clima
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("weatherCache")
        ));
        return cacheManager;
    }
}
