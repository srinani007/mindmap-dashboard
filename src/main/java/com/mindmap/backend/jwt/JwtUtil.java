package com.mindmap.backend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Inject from application.properties:
    // jwt.secret=venkatSecretKeyReplaceWithAReallyLongRandomStringInProd
    // jwt.expiration-ms=86400000
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate a JWT containing username as subject, with issued/expiration timestamps.
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract the subject (username/email) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract any claim via provided resolver function.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token: subject must match and it must not be expired.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        try {
            return validateToken(jwtToken, userDetails);
        } catch (Exception e) {
            // Log the exception if needed
            return false;
        }
    }
}
