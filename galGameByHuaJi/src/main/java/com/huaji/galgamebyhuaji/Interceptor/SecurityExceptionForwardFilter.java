//package com.huaji.galgamebyhuaji.Interceptor;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class SecurityExceptionForwardFilter extends OncePerRequestFilter {
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request,
//	                                HttpServletResponse response,
//	                                FilterChain filterChain) throws ServletException, IOException {
//		try {
//			filterChain.doFilter(request, response);
//		} catch (Exception ex) {
//			request.setAttribute("securityException", ex);
//			RequestDispatcher dispatcher = request.getRequestDispatcher("/api/error/dispose");
//			dispatcher.forward(request, response);
//		}
//	}
//}
