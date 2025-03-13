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

    private final UserDetailsServiceImpl userDetailsService; // Use your service
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthenticationFilter(UserDetailsServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractAccessTokenFromCookie(request);

        if (StringUtils.hasText(token)) {
            try {
                System.out.println("ValidateTokenFromCookie: " + token);

                if (jwtTokenProvider.validateToken(token)) {
                    String username = jwtTokenProvider.getUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    System.out.println("invalid token");
                }
            } catch (ExpiredJwtException ex) { // Specifically catch ExpiredJwtException
                // Token is expired.  Let it fall through to the filter chain.
                // The AuthenticationEntryPoint will handle sending the 401.
                // But, crucially, we do *not* set the SecurityContext, so the user
                // will not be considered authenticated.
                System.out.println("Token expired: " + ex.getMessage()); // Log for debugging
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"message\":\"Session expired. Please login again.\"}");
                out.flush();
                return; // Stop processing here.  Don't continue the filter chain.

            } catch (Exception ex) {
                // Handle other token validation exceptions
                logger.error("Could not set user authentication in security context", ex);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"message\":\"Invalid Token\"}");
                out.flush();
                return; // Stop processing.
            }
        }

        filterChain.doFilter(request, response); // Always continue the filter chain
    }

    private String extractAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}