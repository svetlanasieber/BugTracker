package com.bugtracker.bugtracker.config.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ⚙️ Annotation за technical permissions
 * 
 * Позволява достъп на потребители с technical роли:
 * - ROLE_DEVELOPER
 * - ROLE_QA
 * 
 * Използва се за функции свързани с техническа работа,
 * coding, testing, debugging, и т.н.
 * 
 * Пример:
 * @AuthorizeTechnical
 * public void accessDevelopmentTools() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('ROLE_DEVELOPER') or hasAuthority('ROLE_QA')")
public @interface AuthorizeTechnical {
} 