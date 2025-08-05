package com.bugtracker.bugtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for REST communication with external services.
 * Provides RestTemplate bean for microservice integration.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate bean for HTTP communication with microservices.
     * 
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 