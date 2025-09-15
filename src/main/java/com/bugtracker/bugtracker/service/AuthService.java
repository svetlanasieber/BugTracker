package com.bugtracker.bugtracker.service;


public interface AuthService {

   
    String getCurrentUsername();

    Long getCurrentUserId();

    boolean isCurrentUserAdmin();
} 
