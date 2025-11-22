package com.example.onboardingqueue.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminTokenInterceptor implements HandlerInterceptor {

    private final AppProperties appProperties;

    public AdminTokenInterceptor(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/admin")) {
            return true;
        }

        String token = request.getHeader("X-Admin-Token");
        if (token != null && token.equals(appProperties.getAdminToken())) {
            return true;
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("Missing or invalid admin token");
        return false;
    }
}
