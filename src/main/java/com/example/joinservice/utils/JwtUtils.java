package com.example.joinservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    @Value("${token.secret}")
    private String secretKey;

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    @NotNull
    public String getMemberId(String token) {
        return getClaims(token).getSubject();
    }

    public String extractMemberId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String jwtToken = authorization.replace("Bearer", "");

        return getMemberId(jwtToken);
    }
}
