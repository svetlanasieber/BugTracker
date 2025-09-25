package com.bugtracker.bugtracker.config.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation за bug assignment permissions
 * 
 * Позволява достъп на потребители, които могат да assign-ват bugs:
 * - ROLE_ADMINISTRATOR (може всичко)
 * - ROLE_PROJECT_MANAGER (координира работата)
 * - ROLE_QA (assign-ва bugs след откриването им)
 * 
 * Използва се за функции свързани с присвояване на бъгове
 * на developers, промяна на assignee, и т.н.
 * 
 * Пример:
 * @AuthorizeBugAssignment
 * public void assignBugToDeveloper(Long bugId, Long developerId) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_PROJECT_MANAGER') or hasAuthority('ROLE_QA')")
public @interface AuthorizeBugAssignment {
} 