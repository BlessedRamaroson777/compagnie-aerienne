package com.airline.airline_app.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class RequestContextControllerAdvice {
    
    @ModelAttribute("currentUri")
    public String addRequestUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
