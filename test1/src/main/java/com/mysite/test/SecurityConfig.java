package com.mysite.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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
					.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
			
			.formLogin((formLogin) -> formLogin
				    .loginPage("/member/login")
				    .usernameParameter("loginId") 
				    .defaultSuccessUrl("/"))
			
			.logout((logout) -> logout
					.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true))
//;
		
		//ㅌㅅㅌ
        .csrf(cs -> cs.disable())

        // 여기 허용할 엔드포인트를 전부 나열 
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/schedules/**",
                "/symptoms/**"
                ,"/reminders/**"
            ).permitAll()
            .anyRequest().permitAll()
        )

        // 폼 로그인/세션 필요 없으면 끄기
        .formLogin(form -> form.disable())

        // 필요 시 Basic으로 간단 테스트 가능 (켜도 되고 안 켜도 됨)
        .httpBasic(Customizer.withDefaults());
		//ㅌㅅㅌ
			
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