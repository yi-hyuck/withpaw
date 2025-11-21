package com.mysite.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
					// 챗봇 관련 경로는 인증된 사용자만 접근 가능하도록 설정 추가
					.requestMatchers(new AntPathRequestMatcher("/chatbot")).authenticated()
					.requestMatchers(new AntPathRequestMatcher("/api/chatbot/ask")).authenticated()
					
					
					.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
			
			.formLogin((formLogin) -> formLogin
				    .loginPage("/member/login")
				    .usernameParameter("loginId") 
				    .defaultSuccessUrl("/"))
			
			.logout((logout) -> logout
					.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true))
			;
			
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authrnticationConfiguration) throws Exception {
		return authrnticationConfiguration.getAuthenticationManager();
	}

}