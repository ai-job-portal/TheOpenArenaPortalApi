package com.openarena.openarenaportalapi.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;  // Access token expiration

    @Value("${jwt.refreshExpirationMs}") // New: Refresh token expiration
    private long refreshExpirationMs;

    private static final String ACCESS_TOKEN_KEY = "accessToken"; // Consistent key
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationMs);
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
        return token;
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key(), SignatureAlgorithm.HS256) // Use a strong algorithm
                .compact();
    }


    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        String username = claims.getSubject();
        return username;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException ex) {
            //            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
            return false;
        } catch (ExpiredJwtException ex) {
//            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Expired JWT token");
            return false;
        } catch (UnsupportedJwtException ex) {
//            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
            return false;
        } catch (IllegalArgumentException ex) {
//            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "JWT claims string is empty.");
            return false;
        }
    }
}