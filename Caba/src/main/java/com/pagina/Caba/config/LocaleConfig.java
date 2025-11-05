package com.pagina.Caba.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Configuración de Internacionalización (i18n)
 * 
 * Esta clase configura el soporte multiidioma para la aplicación CABA Pro.
 * Soporta Español (es) e Inglés (en) con cambio dinámico de idioma.
 * 
 * Uso:
 * - Para cambiar de idioma, agregar parámetro ?lang=es o ?lang=en a cualquier URL
 * - En plantillas Thymeleaf usar: th:text="#{clave.mensaje}"
 * - En controladores usar MessageSource para obtener mensajes
 * 
 * @author CABA Pro Team
 * @version 1.0
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {
    
    /**
     * Define el resolver de locale que maneja el idioma actual del usuario.
     * Utiliza SessionLocaleResolver para mantener el idioma seleccionado
     * en la sesión del usuario.
     * 
     * @return LocaleResolver configurado con español como idioma predeterminado
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        
        // Establecer español como idioma predeterminado
        resolver.setDefaultLocale(Locale.forLanguageTag("es"));
        
        return resolver;
    }
    
    /**
     * Configura el interceptor que detecta el parámetro 'lang' en las URLs
     * y cambia el idioma de la aplicación dinámicamente.
     * 
     * Ejemplo de uso:
     * - http://localhost:8080/admin/dashboard?lang=es (Cambiar a español)
     * - http://localhost:8080/arbitro/perfil?lang=en (Cambiar a inglés)
     * 
     * @return LocaleChangeInterceptor configurado
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        
        // Nombre del parámetro que se usa para cambiar el idioma
        interceptor.setParamName("lang");
        
        // Ignorar locale inválidos (si se pasa un idioma no soportado)
        interceptor.setIgnoreInvalidLocale(true);
        
        return interceptor;
    }
    
    /**
     * Configura la fuente de mensajes para la internacionalización.
     * Lee los archivos messages_es.properties y messages_en.properties
     * 
     * @return MessageSource configurado
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = 
            new ReloadableResourceBundleMessageSource();
        
        // Ubicación de los archivos de propiedades (sin extensión ni sufijo de idioma)
        messageSource.setBasename("classpath:messages");
        
        // Codificación UTF-8 para soportar caracteres especiales (ñ, tildes, etc.)
        messageSource.setDefaultEncoding("UTF-8");
        
        // Caché de mensajes (en producción usar tiempo mayor, ej: 3600)
        // -1 = sin caché (útil en desarrollo para ver cambios inmediatamente)
        messageSource.setCacheSeconds(3600);
        
        // Si no encuentra una clave, usar el código de la clave como fallback
        messageSource.setUseCodeAsDefaultMessage(true);
        
        return messageSource;
    }
    
    /**
     * Registra el interceptor de cambio de locale en el registry de Spring MVC.
     * Esto permite que el interceptor sea ejecutado en cada petición HTTP.
     * 
     * @param registry Registry de interceptores de Spring MVC
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
