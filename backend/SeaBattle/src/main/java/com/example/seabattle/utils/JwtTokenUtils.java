package com.example.seabattle.utils;

import com.example.seabattle.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        String role = user.getRole().getName();
        Long id = user.getId();

        claims.put("role", role);
        claims.put("id", id);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Long getId(String token) {
        return getAllClaimsFromToken(token).get("id", Long.class);
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public String getRole(String token) {
        return getAllClaimsFromToken(token).get("role", String.class);
    }

    public boolean validateToken(String bearerToken) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(bearerToken.substring(7))
                    .getBody();
            return true;
        } catch (SignatureException | JsonParseException signatureException) {
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

}
