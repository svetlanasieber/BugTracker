package com.bugtracker.config;

import feign.Request;
import feign.Response;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class FeignConfig {

    @Bean
    public Request.Options requestOptions() {
        long connectTimeoutMillis = TimeUnit.SECONDS.toMillis(5);
        long readTimeoutMillis = TimeUnit.SECONDS.toMillis(10);
        
        log.info("Feign client configured with timeouts - connect: {}ms, read: {}ms", 
                connectTimeoutMillis, readTimeoutMillis);
        
        return new Request.Options(connectTimeoutMillis, TimeUnit.MILLISECONDS, 
                                   readTimeoutMillis, TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Retryer retryer() {
        log.info("Feign client configured with NO retry policy (fail fast)");
        return Retryer.NEVER_RETRY;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }

    @Slf4j
    public static class CustomFeignErrorDecoder implements ErrorDecoder {
        
        private final ErrorDecoder defaultDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            int status = response.status();
            String requestUrl = response.request().url();
            
            log.error("Feign client error - Method: {}, Status: {}, URL: {}", 
                    methodKey, status, requestUrl);

            switch (status) {
                case 400:
                    log.error("Bad Request to project-service: {}", methodKey);
                    return new IllegalArgumentException(
                        "Invalid request to Project Service: " + extractErrorMessage(response)
                    );
                    
                case 404:
                    log.error("Resource not found in project-service: {}", methodKey);
                    return new ResourceNotFoundException(
                        "Resource not found in Project Service: " + extractErrorMessage(response)
                    );
                    
                case 500:
                    log.error("Internal server error in project-service: {}", methodKey);
                    return new ServiceUnavailableException(
                        "Project Service encountered an internal error. Please try again later."
                    );
                    
                case 503:
                    log.error("Project service unavailable: {}", methodKey);
                    return new ServiceUnavailableException(
                        "Project Service is currently unavailable. Please try again later."
                    );
                    
                default:
                    log.error("Unexpected error from project-service: {} (status: {})", methodKey, status);
                    return defaultDecoder.decode(methodKey, response);
            }
        }

        private String extractErrorMessage(Response response) {
            try {
                if (response.body() != null) {
                    return "Error details available in response";
                }
            } catch (Exception e) {
                log.warn("Could not extract error message from response", e);
            }
            return "No additional error details available";
        }
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class ServiceUnavailableException extends RuntimeException {
        public ServiceUnavailableException(String message) {
            super(message);
        }
    }
}

