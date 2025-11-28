package com.mysite.test;

import java.util.Collections;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {
    private String secret;
    private SecretKey key;
    private final long tokenValidityInMilliseconds = 1000L * 60 * 60 * 24; // 24시간
    
    @PostConstruct
    protected void init() {
    	this.key = Jwts.SIG.HS256.key().build();
//        byte[] keyBytes = Decoders.BASE64URL.decode(secret);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    
    public SecretKey getSigningKey() {
    	return this.key;
    }

    // 1. 토큰 생성
    public String createToken(String loginId) {
        Claims claims = Jwts.claims().setSubject(loginId).build();
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        // 토큰에서 loginId 추출
    	String loginId = Jwts.parser().verifyWith(key).build().parseClaimsJws(token).getBody().getSubject();
        
        // 실제 프로젝트에서는 loginId로 UserDetails를 로드해야 함
        // 여기서는 예시를 위해 간단한 인증 객체 생성
        UserDetails userDetails = new User(loginId, "", Collections.emptyList()); 
        
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 3. 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
    
    // 4. Request Header에서 토큰 정보 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    public String createToken(Authentication authentication) {
        // 1. Authentication 객체에서 사용자의 loginId를 가져옵니다.
        String loginId = authentication.getName();
        
        // 2. 실제 토큰 생성 로직을 담고 있는 기존 createToken(String) 메서드를 호출합니다.
        return createToken(loginId); 
    }
}
