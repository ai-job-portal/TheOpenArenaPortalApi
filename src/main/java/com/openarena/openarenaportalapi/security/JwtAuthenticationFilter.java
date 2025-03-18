// src/main/java/com/openarena/openarenaportal/security/JwtAuthenticationFilter.java

package com.openarena.openarenaportalapi.security;

import com.openarena.openarenaportalapi.service.UserDetailsServiceImpl;
import com.openarena.openarenaportalapi.util.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_KEY = "accessToken"; // Key for session storage

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthenticationFilter(UserDetailsServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Get session, don't create if it doesn't exist
        String token = null;

        if (session != null) {
            token = (String) session.getAttribute(ACCESS_TOKEN_KEY);
        }


        if (StringUtils.hasText(token)) {
            try {
                // Validate the token
                if (!jwtTokenProvider.validateToken(token)) {
                    throw new ExpiredJwtException(null, null, "Token has expired"); // Trigger expired case
                }

                String username = jwtTokenProvider.getUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response); // Continue the filter chain
                return;

            } catch (ExpiredJwtException ex) {
                // Handle expired token (could also remove from session here if desired)
                if (session != null) {
                    session.removeAttribute(ACCESS_TOKEN_KEY); // Remove expired token
                }
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"message\":\"Session expired. Please login again.\"}");
                out.flush();
                return;
            } catch (Exception ex) {
                // Handle other token validation exceptions
                logger.error("Could not set user authentication in security context", ex);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"message\":\"Invalid Token\"}");
                out.flush();
                return;
            }
        }

        filterChain.doFilter(request, response);  // No token, or token processed
    }


    // No longer needed, we're using session storage.
    // private String extractAccessTokenFromCookie(HttpServletRequest request) { ... }
}