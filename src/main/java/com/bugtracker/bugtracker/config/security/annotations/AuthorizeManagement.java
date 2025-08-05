package com.bugtracker.bugtracker.config.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 🏢 Annotation за management permissions
 * 
 * Позволява достъп на потребители с management роли:
 * - ROLE_ADMINISTRATOR
 * - ROLE_PROJECT_MANAGER
 * 
 * Използва се за функции свързани с управление на проекти,
 * планиране, координация на екипа, и т.н.
 * 
 * Пример:
 * @AuthorizeManagement
 * public void planProjectDeadlines() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_PROJECT_MANAGER')")
public @interface AuthorizeManagement {
} 