package com.bugtracker.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
public class RateLimitingConfig {

    @Bean
    public FilterRegistrationBean<LoginRateLimitFilter> rateLimitFilter() {
        FilterRegistrationBean<LoginRateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoginRateLimitFilter());
        registrationBean.addUrlPatterns("/api/v1/auth/login", "/api/auth/login");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        log.info("Login rate limiting filter registered for /api/v1/auth/login and /api/auth/login");
        return registrationBean;
    }

    @Slf4j
    public static class LoginRateLimitFilter implements Filter {
        
        private static final int MAX_ATTEMPTS = 5;
        private static final Duration WINDOW_DURATION = Duration.ofMinutes(15);
        private static final Duration CLEANUP_INTERVAL = Duration.ofMinutes(30);
        
        private final Map<String, LoginAttempts> attemptCache = new ConcurrentHashMap<>();
        private Instant lastCleanup = Instant.now();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            if (!"POST".equalsIgnoreCase(httpRequest.getMethod())) {
                chain.doFilter(request, response);
                return;
            }
            
            String clientIp = getClientIp(httpRequest);
            
            cleanupOldEntries();
            
            if (isRateLimited(clientIp)) {
                log.warn("Login rate limit exceeded for IP: {}", clientIp);
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(String.format(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Maximum %d login attempts exceeded. Please try again in %d minutes.\",\"timestamp\":\"%s\"}",
                    MAX_ATTEMPTS,
                    WINDOW_DURATION.toMinutes(),
                    Instant.now()
                ));
                return;
            }
            
            recordAttempt(clientIp);
            
            chain.doFilter(request, response);
        }

        private boolean isRateLimited(String ip) {
            LoginAttempts attempts = attemptCache.get(ip);
            
            if (attempts == null) {
                return false;
            }
            
            if (attempts.isExpired()) {
                attemptCache.remove(ip);
                return false;
            }
            
            return attempts.count >= MAX_ATTEMPTS;
        }

        private void recordAttempt(String ip) {
            attemptCache.compute(ip, (key, existing) -> {
                if (existing == null || existing.isExpired()) {
                    log.debug("New rate limit window started for IP: {}", ip);
                    return new LoginAttempts(1, Instant.now());
                }
                existing.count++;
                log.debug("Login attempt {} of {} for IP: {}", existing.count, MAX_ATTEMPTS, ip);
                return existing;
            });
        }

        private void cleanupOldEntries() {
            Instant now = Instant.now();
            if (Duration.between(lastCleanup, now).compareTo(CLEANUP_INTERVAL) > 0) {
                int sizeBefore = attemptCache.size();
                attemptCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
                int sizeAfter = attemptCache.size();
                log.info("Rate limit cache cleanup: removed {} expired entries ({} -> {})", 
                        sizeBefore - sizeAfter, sizeBefore, sizeAfter);
                lastCleanup = now;
            }
        }

        private String getClientIp(HttpServletRequest request) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        }

        private static class LoginAttempts {
            int count;
            Instant firstAttempt;

            LoginAttempts(int count, Instant firstAttempt) {
                this.count = count;
                this.firstAttempt = firstAttempt;
            }

            boolean isExpired() {
                return Duration.between(firstAttempt, Instant.now()).compareTo(WINDOW_DURATION) > 0;
            }
        }
    }
}

