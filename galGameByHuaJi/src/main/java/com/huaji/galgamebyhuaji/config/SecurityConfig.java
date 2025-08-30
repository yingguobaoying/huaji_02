package com.huaji.galgamebyhuaji.config;

import com.huaji.galgamebyhuaji.Interceptor.BasicsFilter;
import com.huaji.galgamebyhuaji.Interceptor.TouristFilter;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	
		@Bean
		public SecurityFilterChain filterChain (HttpSecurity http,
		                                        BasicsFilter basicsFilter,
		                                        TouristFilter touristFilter) throws Exception {
		http
				// 只匹配 /api/** 的请求
				.securityMatcher("/api/**")
				.sessionManagement(session -> session
						.maximumSessions(1)
						.maxSessionsPreventsLogin(true))
				
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				
				.authorizeHttpRequests(auth -> auth
								.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
								.requestMatchers(
										"/favicon.ico",
										"/static/css/**",
										"/static/js/**",
										"/static/img/**",
										"/static/**",
										"/public/vue/**"
								                ).permitAll()
								.requestMatchers("/api/login/**", "/api/register/**").permitAll()
								.requestMatchers("/api/user/**").hasAnyRole(
										JurisdictionLevel.getNeedJurisdiction(JurisdictionLevel.NOT_VALIDATED.getLevel())
								                                           )
								.requestMatchers("/api/download/**").hasAnyRole(
										JurisdictionLevel.getNeedJurisdiction(JurisdictionLevel.USERS_JURISDICTION.getLevel())
								                                               )
								.anyRequest().permitAll()
				                      )
				.csrf(AbstractHttpConfigurer::disable);
		http.addFilterBefore(basicsFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterAfter(touristFilter, BasicsFilter.class);
		return http.build();
	}
	
	/**
	 * 静态资源完全不走 SecurityFilterChain，性能最高
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer () {
		return (web) -> web.ignoring().requestMatchers(
				"/favicon.ico",
				"/static/**",  // 统一使用这个模式
				"/public/**",
				"/static/css/**",
				"/static/js/**",
				"/static/img/**",
				"/public/vue/**",
				"/**.js",              // 所有JS文件
				"/**.css",             // 所有CSS文件
				"/**.png",
				"/**.jpg",
				"/**.jpeg",
				"/**.gif",
				"/**.ico",
				"/**.ts"
		                                              );
	}
}