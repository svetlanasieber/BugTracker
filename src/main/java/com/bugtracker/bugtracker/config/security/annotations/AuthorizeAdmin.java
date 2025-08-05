package com.bugtracker.bugtracker.config.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 👑 Annotation за достъп само за администратори
 * 
 * Използва се на методи или класове, които трябва да бъдат достъпни
 * само за потребители с роля ROLE_ADMINISTRATOR.
 * 
 * Пример:
 * @AuthorizeAdmin
 * public void deleteUser(Long userId) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public @interface AuthorizeAdmin {
} 