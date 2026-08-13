package com.kalman.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allow the Angular dev server (localhost:4200) to call the API.
 *
 * In production, replace the allowed origin with your deployed frontend URL,
 * or remove this class entirely and handle CORS at the reverse-proxy layer.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")   // Angular dev server
                .allowedMethods("GET", "POST", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // needed for session cookie (JSESSIONID)
                .maxAge(3600);
    }
}
