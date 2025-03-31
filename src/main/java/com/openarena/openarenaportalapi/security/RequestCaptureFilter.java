package com.openarena.openarenaportalapi.security;

import com.openarena.openarenaportalapi.util.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestCaptureFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            RequestContextHolder.setRequest(request);
            filterChain.doFilter(request, response);
        } finally {
            RequestContextHolder.clear();
        }
    }
}