package com.huaji.galgamebyhuaji.vignaAiFrame.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class VignaHttpClientFactory {
	private static final List<Closeable> all = new LinkedList<>();
	
	/**
	 * 创建http客户端
	 *
	 * @param maxMilliseconds 超时时间,单位毫秒
	 */
	public static CloseableHttpClient createHttpClient (Long maxMilliseconds) {
		//仅在加载客户端/刷新客户端时使用,除了管理员偶尔刷新配置以及服务器启动时应该不会被用到
		PoolingHttpClientConnectionManager connectionManager =
				PoolingHttpClientConnectionManagerBuilder.create()
						.setMaxConnTotal(20) // 最大总连接数
						.setMaxConnPerRoute(5) // 每个路由的最大连接数
						.setDefaultConnectionConfig( // 连接级别配置
								ConnectionConfig.custom()
										.setValidateAfterInactivity(Timeout.ofSeconds(60))//60s连接过期
										.setConnectTimeout(Timeout.ofSeconds(10)) // TCP连接超时
										.setSocketTimeout(Timeout.ofSeconds(30))  // Socket读取超时
										.build())
						.build();
		//配置请求级别参数
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofSeconds(5)) // 从连接池获取连接的超时时间
				.setResponseTimeout(Timeout.ofMilliseconds(
						maxMilliseconds == null ? 300 * 1000 : maxMilliseconds))        // 等待响应超时考虑到ai可能会出超长响应这里默认设置为5分钟
				.build();
		CloseableHttpClient build = HttpClients.custom()
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig)
				.build();
		all.add(build);
		return build;
	}
	
	public static void shutdown () {
		for ( Closeable c : all ) {
			try {
				c.close();
			} catch ( IOException e ) {
				log.error("关闭http客户端失败{}", e.getMessage(), e);
			}
		}
	}
	
	public static CloseableHttpAsyncClient createAsyncHttpClient (Long maxMilliseconds) {
		// 连接池配置
		PoolingAsyncClientConnectionManager connectionManager =
				PoolingAsyncClientConnectionManagerBuilder.create()
						.setMaxConnTotal(20)
						.setMaxConnPerRoute(5)
						.setDefaultConnectionConfig(
								ConnectionConfig.custom()
										.setConnectTimeout(Timeout.ofSeconds(10))
										.setSocketTimeout(Timeout.ofSeconds(30))
										.build())
						.build();
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofSeconds(5))
				.setResponseTimeout(Timeout.ofMilliseconds(
						maxMilliseconds == null ? 300_000 : maxMilliseconds))
				.build();
		
		CloseableHttpAsyncClient client = HttpAsyncClients.custom()
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig)
				.build();
		client.start();  // 启动异步客户端
		all.add(client); // 纳入统一管理,便于服务器关闭时统一释放
		return client;
	}
}
