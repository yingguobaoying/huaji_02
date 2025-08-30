package com.huaji.galgamebyhuaji.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huaji.galgamebyhuaji.Interceptor.LoginInterceptor;
import com.huaji.galgamebyhuaji.constant.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableCaching // 开启缓存
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class SpringMvcConfig implements WebMvcConfigurer {
	static {
		System.err.println("********************************** SpringMvcConfig 加载 ***************************************");
	}
	
	@Autowired
	LoginInterceptor loginInterceptor;
	
	@Override
	public void addCorsMappings (CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:5173")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.exposedHeaders(SystemConstant.JWT_TOKEN_NAME, SystemConstant.JWT_TOKEN_NAME, SystemConstant.SYSTEM_MSG_URL) // 加上你实际使用的 header 名称
				.maxAge(3600);
	}
	
	@Value("${resource-save-path}")
	private String resourceSavePath;
	
	@Override
	public void addResourceHandlers (ResourceHandlerRegistry registry) {
		// 静态资源
		registry.addResourceHandler("/static/**")
				.addResourceLocations(
						"file:" + resourceSavePath + "/",
						"classpath:/static/"
				                     );
		//监控页
		registry.addResourceHandler("/druid/**")
				.addResourceLocations("classpath:/META-INF/resources/");
		// Vue应用文件
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/public/vue/")
		;
	}
	
	@Override
	public void addInterceptors (InterceptorRegistry registry) {
//        registry.addInterceptor(basicsIntercept)
//                .addPathPatterns("/**") //添加拦截路径
//                .excludePathPatterns("/login", "/static/**");//排除的路径
		registry.addInterceptor(loginInterceptor)
				.addPathPatterns("/login/**", "/register/**")
				.excludePathPatterns(
						"/static/**",
						"/druid/**",
						"/error",
						"/favicon.ico"
				                    );
	}
	
	@Bean
	public ObjectMapper objectMapper () {
		// Jackson 的默认配置
		return new ObjectMapper();
	}
	
	@Bean
	public StandardServletMultipartResolver multipartResolver () {
		return new StandardServletMultipartResolver();
	}
	
	@Bean
	public DataSourceTransactionManager transactionManager (DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean
	public RestTemplate restTemplate () {
		return new RestTemplate();
	}
	
}