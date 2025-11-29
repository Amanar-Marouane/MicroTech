package com.restapi.microtech.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.restapi.microtech.entity.enums.UserRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        UserRole role = (UserRole) session.getAttribute("ROLE");

        if (!UserRole.ADMIN.equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403
            return false;
        }

        return true;
    }
}
