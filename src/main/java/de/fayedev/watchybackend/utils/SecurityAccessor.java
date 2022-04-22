package de.fayedev.watchybackend.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityAccessor {

    private SecurityAccessor() {
    }

    public static String getAuthenticatedUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
