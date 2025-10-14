package com.huaji.galgamebyhuaji.config;

import com.huaji.galgamebyhuaji.Interceptor.BasicAuthenticationFilter;
import com.huaji.galgamebyhuaji.Interceptor.CustomAccessDeniedHandler;
import com.huaji.galgamebyhuaji.Interceptor.StrongAuthenticationEntryPoint;
import com.huaji.galgamebyhuaji.Interceptor.StrongAuthenticationFilter;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityConfig {
	
	private final LoginService loginService;
	
	private final UserMxgServlet userMxgServlet;
	
	private final StrongAuthenticationEntryPoint strongAuthenticationEntryPoint;
	
	private final CustomAccessDeniedHandler accessDeniedHandler;
	final
	TokenService tokenService;
	
	public SecurityConfig(LoginService loginService, UserMxgServlet userMxgServlet, StrongAuthenticationEntryPoint strongAuthenticationEntryPoint, CustomAccessDeniedHandler accessDeniedHandler, TokenService tokenService) {
		this.loginService = loginService;
		this.userMxgServlet = userMxgServlet;
		this.strongAuthenticationEntryPoint = strongAuthenticationEntryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
		this.tokenService = tokenService;
	}
	
	@Bean
	public BasicAuthenticationFilter basicAuthenticationFilter() {
		return new BasicAuthenticationFilter(loginService, userMxgServlet);
	}
	
	@Bean
	public StrongAuthenticationFilter strongAuthenticationFilter() {
		return new StrongAuthenticationFilter(tokenService, userMxgServlet);
	}
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http
	) throws Exception {
		http
				// 禁用CSRF
				.csrf(AbstractHttpConfigurer::disable)
				// 添加安全头
				.headers(headers -> headers
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // 按需创建Session
						.maximumSessions(1)  // 单设备登录
						.maxSessionsPreventsLogin(false)  // 允许新登录踢掉旧会话
				)
				// 授权配置
				.authorizeHttpRequests(authz -> authz
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						// 静态资源和公开接口放行
						.requestMatchers("/static/**", "/public/**", "/api/login", "/api/register").permitAll()
						.requestMatchers("/druid/**").hasRole("ROOT_JURISDICTION")
						// 强认证路径需要认证
						.requestMatchers("/api/user/**").authenticated()
						// 其他路径允许匿名访问
						.anyRequest().permitAll()
				)
				// 异常处理
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(strongAuthenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler)
				)
				.addFilterBefore(basicAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(strongAuthenticationFilter(), BasicAuthenticationFilter.class)
		;
		return http.build();
	}
	
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
