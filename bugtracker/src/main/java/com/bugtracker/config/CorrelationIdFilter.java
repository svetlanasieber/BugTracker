package com.bugtracker.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.UUID;

@Configuration
@Slf4j
public class CorrelationIdFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "correlationId";

    @Bean
    public FilterRegistrationBean<RequestCorrelationFilter> requestCorrelationFilter() {
        FilterRegistrationBean<RequestCorrelationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestCorrelationFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        log.info("Correlation ID filter registered for request tracing");
        return registrationBean;
    }

    @Slf4j
    public static class RequestCorrelationFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            try {
                String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
                
                if (correlationId == null || correlationId.isEmpty()) {
                    correlationId = httpRequest.getHeader(REQUEST_ID_HEADER);
                }
                
                if (correlationId == null || correlationId.isEmpty()) {
                    correlationId = generateCorrelationId();
                }
                
                MDC.put(MDC_KEY, correlationId);
                
                httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
                httpResponse.setHeader(REQUEST_ID_HEADER, correlationId);
                
                logRequestDetails(httpRequest, correlationId);
                
                chain.doFilter(request, response);
                
                logResponseDetails(httpResponse, correlationId);
                
            } finally {
                MDC.remove(MDC_KEY);
            }
        }

        private String generateCorrelationId() {
            return UUID.randomUUID().toString();
        }

        private void logRequestDetails(HttpServletRequest request, String correlationId) {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String clientIp = getClientIp(request);
            
            if (queryString != null) {
                uri = uri + "?" + queryString;
            }
            
            log.info("Incoming request: {} {} from IP: {} [correlationId: {}]", 
                    method, uri, clientIp, correlationId);
        }

        private void logResponseDetails(HttpServletResponse response, String correlationId) {
            int status = response.getStatus();
            
            if (status >= 200 && status < 300) {
                log.debug("Request completed successfully: status {} [correlationId: {}]", status, correlationId);
            } else if (status >= 400 && status < 500) {
                log.warn("Client error response: status {} [correlationId: {}]", status, correlationId);
            } else if (status >= 500) {
                log.error("Server error response: status {} [correlationId: {}]", status, correlationId);
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
    }
}

