package com.bugtracker.bugtracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Bug Tracker application.
 * 
 * <p>This configuration class sets up Spring Security with comprehensive security features:</p>
 * <ul>
 *   <li><strong>Authentication:</strong> Form-based login with email as username</li>
 *   <li><strong>Authorization:</strong> Role-based access control (USER, ADMIN)</li>
 *   <li><strong>Password Security:</strong> BCrypt password encoding</li>
 *   <li><strong>CSRF:</strong> Disabled for API endpoints, configurable for production</li>
 *   <li><strong>Public Access:</strong> Login, registration, static resources, API documentation</li>
 * </ul>
 * 
 * <p>The security model supports:</p>
 * <ul>
 *   <li>Method-level security with {@code @PreAuthorize} annotations</li>
 *   <li>Custom user details service for email-based authentication</li>
 *   <li>Graceful logout with redirect to landing page</li>
 *   <li>Integration with Swagger/OpenAPI documentation endpoints</li>
 * </ul>
 * 
 * @author Bug Tracker Team
 * @version 1.0
 * @since 1.0
 * @see UserDetailsService for custom authentication logic
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Активирам @PreAuthorize/@PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    /**
     * Configures the main security filter chain for HTTP requests.
     * 
     * <p>This method sets up the complete security configuration including:</p>
     * <ul>
     *   <li><strong>CSRF Protection:</strong> Disabled for API simplicity (enable for production)</li>
     *   <li><strong>Authorization Rules:</strong> Public endpoints, admin-only areas, authenticated areas</li>
     *   <li><strong>Form Login:</strong> Custom login page with email-based authentication</li>
     *   <li><strong>Logout Handling:</strong> Secure logout with redirect</li>
     * </ul>
     * 
     * <p><strong>Public Endpoints (no authentication required):</strong></p>
     * <ul>
     *   <li>Landing page and authentication pages</li>
     *   <li>Static resources (CSS, JS, images)</li>
     *   <li>WebJars for frontend libraries</li>
     *   <li>Swagger/OpenAPI documentation</li>
     *   <li>Error pages</li>
     * </ul>
     * 
     * <p><strong>Protected Endpoints:</strong></p>
     * <ul>
     *   <li>{@code /admin/**} - Requires ADMIN role</li>
     *   <li>All other endpoints - Requires authentication</li>
     * </ul>
     * 
     * @param http the HttpSecurity configuration object to customize
     * @return the configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity, enable for production if needed
            .authorizeHttpRequests(authorize -> authorize
                // ==================== PUBLIC ENDPOINTS ====================
                .requestMatchers("/", "/auth/login", "/auth/login-process", "/landing", 
                               "/css/**", "/js/**", "/webjars/**", "/images/**", "/error").permitAll()
                
                // ==================== DEVELOPMENT ENDPOINTS ====================
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                .requestMatchers("/debug/**").permitAll() // Debug endpoints за troubleshooting
                .requestMatchers("/test/**").permitAll() // Test endpoints за troubleshooting
                
                // ==================== ROLE-BASED ACCESS CONTROL ====================
                // Administrator access (пълен достъп до admin панел)
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                
                // Management access (Administrator + Project Manager)
                .requestMatchers("/management/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER")
                
                // Technical access patterns
                .requestMatchers("/api/bugs/resolve/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEVELOPER")
                .requestMatchers("/api/bugs/assign/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER", "ROLE_QA")
                .requestMatchers("/api/testing/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_QA")
                .requestMatchers("/api/development/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEVELOPER")
                
                // Project management patterns
                .requestMatchers("/projects/create", "/projects/*/edit", "/projects/*/delete").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER")
                .requestMatchers("/projects/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER", "ROLE_DEVELOPER", "ROLE_QA")
                
                // Bug management patterns (всички authenticated могат да разглеждат)
                .requestMatchers("/bugs/create").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER", "ROLE_QA", "ROLE_DEVELOPER")
                .requestMatchers("/bugs/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login-process")
                .defaultSuccessUrl("/", true)
                .usernameParameter("email") // Changed to match CustomUserDetailsService
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/landing?logout")
                .permitAll()
            );

        return http.build();
    }

    /**
     * Provides a secure password encoder for the application.
     * 
     * <p>Uses BCrypt hashing algorithm which is considered one of the most secure
     * password hashing methods available. BCrypt automatically handles salt generation
     * and includes protection against timing attacks.</p>
     * 
     * <p><strong>Security Features:</strong></p>
     * <ul>
     *   <li>Automatic salt generation for each password</li>
     *   <li>Configurable work factor (currently using default)</li>
     *   <li>Resistant to rainbow table attacks</li>
     *   <li>Slow hashing to prevent brute force attacks</li>
     * </ul>
     * 
     * @return a BCryptPasswordEncoder instance for password hashing and verification
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides the authentication manager for manual authentication operations.
     * 
     * <p>This bean is used when programmatic authentication is needed, such as:</p>
     * <ul>
     *   <li>User registration with automatic login</li>
     *   <li>Password reset flows</li>
     *   <li>API authentication (if implemented)</li>
     *   <li>Custom authentication scenarios</li>
     * </ul>
     * 
     * <p>The authentication manager is configured automatically by Spring Security
     * but exposed as a bean for dependency injection where needed.</p>
     * 
     * @param config the Spring Security authentication configuration
     * @return the configured AuthenticationManager
     * @throws Exception if authentication manager configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
