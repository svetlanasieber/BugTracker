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


public class SecurityConfig {

    private final UserDetailsService userDetailsService;

   
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(authorize -> authorize
              
                .requestMatchers("/", "/auth/login", "/auth/login-process", "/landing", 
                               "/css/**", "/js/**", "/webjars/**", "/images/**", "/error").permitAll()
                
              
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                .requestMatchers("/debug/**").permitAll() 
                .requestMatchers("/test/**").permitAll() 
                
              
                
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                
               
                .requestMatchers("/management/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER")
                
                
                .requestMatchers("/api/bugs/resolve/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEVELOPER")
                .requestMatchers("/api/bugs/assign/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER", "ROLE_QA")
                .requestMatchers("/api/testing/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_QA")
                .requestMatchers("/api/development/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEVELOPER")
                
               
                .requestMatchers("/projects/create", "/projects/*/edit", "/projects/*/delete").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER")
                .requestMatchers("/projects/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER", "ROLE_DEVELOPER", "ROLE_QA")
                
                
                .requestMatchers("/bugs/create").hasAnyAuthority("ROLE_ADMIN", "ROLE_PROJECT_MANAGER", "ROLE_QA", "ROLE_DEVELOPER")
                .requestMatchers("/bugs/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login-process")
                .defaultSuccessUrl("/", true)
                .usernameParameter("email") 
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/landing?logout")
                .permitAll()
            );

        return http.build();
    }

    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
