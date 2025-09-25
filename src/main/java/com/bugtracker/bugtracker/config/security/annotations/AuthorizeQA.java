package com.bugtracker.bugtracker.config.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation за достъп само за QA Engineers
 * 
 * Използва се на методи или класове, които трябва да бъдат достъпни
 * само за потребители с роля ROLE_QA.
 * 
 * Пример:
 * @AuthorizeQA
 * public void testBugFix(Long bugId) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('ROLE_QA')")
public @interface AuthorizeQA {
} 