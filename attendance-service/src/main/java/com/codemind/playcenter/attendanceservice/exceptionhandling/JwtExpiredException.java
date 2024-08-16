package com.codemind.playcenter.attendanceservice.exceptionhandling;

import org.springframework.security.core.AuthenticationException;

public class JwtExpiredException extends AuthenticationException {
    
    public JwtExpiredException(String msg) {
        super(msg);
    }

    public JwtExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
