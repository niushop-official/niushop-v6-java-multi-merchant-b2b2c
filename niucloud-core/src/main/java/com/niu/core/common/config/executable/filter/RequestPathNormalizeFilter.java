package com.niu.core.common.config.executable.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Normalize repeated slashes in request paths so legacy frontend URLs like
 * /api//site/category/tree can still reach the mapped controller.
 */
@Component
@Order(-200)
public class RequestPathNormalizeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String normalizedUri = normalizePath(requestUri);

        if (!requestUri.equals(normalizedUri)) {
            RequestDispatcher dispatcher = request.getRequestDispatcher(normalizedUri);
            dispatcher.forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        return path.replaceAll("/{2,}", "/");
    }
}
