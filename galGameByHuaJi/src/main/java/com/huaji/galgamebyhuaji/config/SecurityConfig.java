package com.huaji.galgamebyhuaji.config;

import com.huaji.galgamebyhuaji.Interceptor.BasicAuthenticationFilter;
import com.huaji.galgamebyhuaji.Interceptor.CustomAccessDeniedHandler;
import com.huaji.galgamebyhuaji.Interceptor.StrongAuthenticationEntryPoint;
import com.huaji.galgamebyhuaji.Interceptor.StrongAuthenticationFilter;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private UserMxgServlet userMxgServlet;
	
	@Autowired
	private StrongAuthenticationEntryPoint strongAuthenticationEntryPoint;
	
	@Autowired
	private CustomAccessDeniedHandler accessDeniedHandler;
	@Autowired
	TokenService tokenService;
	
	// 手动创建过滤器Bean
	@Bean
	@Order(1) // 添加顺序注解
	public BasicAuthenticationFilter basicAuthenticationFilter() {
		return new BasicAuthenticationFilter(loginService, userMxgServlet);
	}
	
	@Bean
	@Order(2) // 添加顺序注解
	public StrongAuthenticationFilter strongAuthenticationFilter() {
		return new StrongAuthenticationFilter(tokenService, userMxgServlet, strongAuthenticationEntryPoint);
	}
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http
	) throws Exception {
		http
				// 禁用CSRF
				.csrf(csrf -> csrf.disable())
				// 会话管理 - 保持无状态
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				// 授权配置
				.authorizeHttpRequests(authz -> authz
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						// 静态资源和公开接口放行
						.requestMatchers("/static/**", "/public/**", "/api/login", "/api/register").permitAll()
						// 强认证路径需要认证
						.requestMatchers("/api/user/**").authenticated()
						// 其他路径允许匿名访问（由基础认证过滤器处理）
						.anyRequest().permitAll()
				)
				// 异常处理
				.exceptionHandling(exceptions -> exceptions
						// 认证入口点 - 区分强认证和普通认证
						.authenticationEntryPoint((request, response, authException) -> {
							if (request.getServletPath().startsWith("/api/user/")) {
								// 强认证路径使用专门的入口点
								strongAuthenticationEntryPoint.commence(request, response, authException);
							} else {
								response.setStatus(HttpStatus.UNAUTHORIZED.value());
							}
						})
						// 访问拒绝处理器
						.accessDeniedHandler(accessDeniedHandler)
				)
				.addFilterBefore(basicAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(strongAuthenticationFilter(), BasicAuthenticationFilter.class)
		;
		return http.build();
	}
	
	@Bean
	public AccessDeniedHandler GlobalAccessDeniedHandler(){}
	/**
	 * 静态资源完全不走 SecurityFilterChain，性能最高
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
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
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		// 不使用密码认证，使用无操作编码器
		return NoOpPasswordEncoder.getInstance();
	}
}
