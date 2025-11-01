package com.bugtracker.config;

import com.bugtracker.config.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    FileStorageProperties.class,
    JwtProperties.class
})
    
public class PropertiesConfig {
}

