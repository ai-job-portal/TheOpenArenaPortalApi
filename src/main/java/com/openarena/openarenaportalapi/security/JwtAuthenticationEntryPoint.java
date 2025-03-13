package com.openarena.openarenaportalapi.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();

        // Customize the error message here
        String message = "Full authentication is required to access this resource."; // Default message

        //  Check if the request is for a known API endpoint. If not return custom message.
        if (request.getRequestURI().startsWith("/api/") && !isKnownEndpoint(request.getRequestURI())) { //add /api
            message = "The requested resource could not be found.";
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Set 404 for unknown API endpoints

        }

        writer.println("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }

    private boolean isKnownEndpoint(String requestURI) {
        // Implement logic to check if the requestURI matches a *known* endpoint.
        // This is a simplified example.  A robust solution would use Spring's
        // RequestMappingInfo, but that requires injecting the RequestMappingHandlerMapping,
        // which adds complexity.
        return requestURI.startsWith("/api/auth/") ||
                requestURI.startsWith("/api/users/me") ||
                requestURI.startsWith("/api/jobseekers/profile")|| // Add more as we develop the api
                requestURI.startsWith("/api/jobs");
        // Add other known API paths here
    }
}