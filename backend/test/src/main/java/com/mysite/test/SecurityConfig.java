package com.mysite.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
    private AuthenticationConfiguration authenticationConfiguration;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
//	@Bean
//    public JsonUsernamePasswordAuthenticationFilter jsonAuthenticationFilter() throws Exception {
//        JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter();
//        
//        // RN에서 POST하는 경로 설정
//        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/member/login", "POST"));
//        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
//        
//        // 성공 핸들러 (기존 formLogin에 있던 로직 재사용)
//        filter.setAuthenticationSuccessHandler((request, response, authentication) -> {
//            response.setStatus(200);
//            response.getWriter().write("{\"message\": \"로그인 성공\"}");
//            response.getWriter().flush();
//        });
//
//        // 실패 핸들러 (기존 formLogin에 있던 로직 재사용)
//        filter.setAuthenticationFailureHandler((request, response, exception) -> {
//            response.setStatus(401);
//            response.getWriter().write("{\"message\": \"아이디 또는 비밀번호가 올바르지 않습니다.\"}");
//            response.getWriter().flush();
//        });
//        
//        // username/password 파라미터 이름 설정 (JSON 파싱 후 필터에 넘길 때 사용됨)
//        filter.setUsernameParameter("loginId"); 
//        filter.setPasswordParameter("password");
//
//        return filter;
//    }
	
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.headers(headers -> headers
					.addHeaderWriter(new XFrameOptionsHeaderWriter(
							XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
					)
			.sessionManagement(session->session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					)
			.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
					.requestMatchers(new AntPathRequestMatcher("/member/login")).permitAll()
		            .requestMatchers(new AntPathRequestMatcher("/member/signup")).permitAll()
		            .requestMatchers(new AntPathRequestMatcher("/member/signup/pet")).permitAll()
		            .requestMatchers(new AntPathRequestMatcher("/api/schedules/**")).permitAll()
		            .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
					
			.formLogin((formLogin) -> formLogin.disable())
//			.addFilterAt(jsonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
			.logout((logout) -> logout.disable());
//					.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
//					.logoutSuccessUrl("/")
//					.invalidateHttpSession(true))
			
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
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 리액트 네이티브가 실행되는 개발 환경 주소 허용 (Android Emulator: 10.0.2.2)
        configuration.setAllowedOrigins(List.of("http://localhost:8081", "http://10.0.2.2:8090", "http://10.0.2.2:8081")); // 필요에 따라 추가
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 쿠키/세션 등을 허용
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
        return source;
    }

}