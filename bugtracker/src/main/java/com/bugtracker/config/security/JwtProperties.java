package com.bugtracker.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
    
public record JwtProperties(
    String secretKey,
    long expirationTimeMs
) {
}

