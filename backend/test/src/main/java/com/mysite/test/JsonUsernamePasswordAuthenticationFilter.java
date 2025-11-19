package com.mysite.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.test.member.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException; // IOException 필요

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 
            throws AuthenticationException { // 💡 throws AuthenticationException 만 남겨야 함
        
        if (!"POST".equals(request.getMethod()) || !request.getContentType().contains("application/json")) {
            return super.attemptAuthentication(request, response);
        }

        LoginRequest loginRequest;
        try {
            // 1. JSON 요청 본문 읽기 (IOException 발생 가능)
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            // 2. IOException을 RuntimeException(IllegalStateException)으로 래핑하여 던짐
            // Spring Security 필터 체인이 이를 적절히 처리할 것입니다.
            throw new IllegalStateException("Failed to parse authentication request body (JSON format)", e); 
        }

        // 3. Spring Security가 이해할 수 있는 AuthenticationToken 생성 (loginId를 사용)
        UsernamePasswordAuthenticationToken authRequest = 
            new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());

        setDetails(request, authRequest); 

        // 4. AuthenticationManager에 인증을 위임합니다.
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}