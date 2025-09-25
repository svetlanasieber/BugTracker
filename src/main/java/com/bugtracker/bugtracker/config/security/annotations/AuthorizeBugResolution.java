package com.bugtracker.bugtracker.config.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation за bug resolution permissions
 * 
 * Позволява достъп на потребители, които могат да resolve-ват bugs:
 * - ROLE_ADMINISTRATOR (може всичко)
 * - ROLE_DEVELOPER (основната им работа е да оправят бъгове)
 * 
 * Използва се за функции свързани с оправяне на бъгове,
 * промяна на статус на "Resolved", затваряне на bugs, и т.н.
 * 
 * Пример:
 * @AuthorizeBugResolution
 * public void resolveBug(Long bugId) { ... }
 * 
 * @AuthorizeBugResolution
 * public void markBugAsFixed(Long bugId, String resolution) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_DEVELOPER')")
public @interface AuthorizeBugResolution {
} 