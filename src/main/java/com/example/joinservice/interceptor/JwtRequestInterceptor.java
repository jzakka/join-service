package com.example.joinservice.interceptor;

import com.example.joinservice.utils.JwtUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtRequestInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String jwtToken = authorization.replace("Bearer ", "");
            String memberId = jwtUtils.getMemberId(jwtToken);

            String inputstream = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(inputstream);

            return memberId.equals(jsonNode.get("memberId").asText());
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }
}
