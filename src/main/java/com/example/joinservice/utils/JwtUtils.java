package com.example.joinservice.utils;

import com.example.joinservice.vo.TokenJoinAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final Environment env;

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(env.getProperty("token.secret"))
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

    public String regenerateToken(String memberId, List<TokenJoinAuthority> joinedGathers) {
        String token = Jwts.builder()
                .setSubject(memberId)
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .claim("gatherIds", joinedGathers)
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        return token;
    }
}
