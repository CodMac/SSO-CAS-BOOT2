package zqit.client.pilgins.cas;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.unicon.cas.client.configuration.EnableCasClient;
import zqit.client.pilgins.cas.filter.AxiosAuthFilter;
import zqit.client.pilgins.cas.filter.CorsFilterConfig;

@Configuration
@EnableCasClient // 开启cas
public class CasConfig {

	@Value("${cas.server-url-prefix}")
	private String CAS_SERVER_URL_PREFIX;
	@Value("${cas.server-login-url}")
	private String CAS_SERVER_LOGIN_URL;
	@Value("${cas.client-host-url}")
	private String CAS_CLIENT_HOST_URL;

	/**
	 * filter顺序 
	 * 	axiosAuthFilter
	 * 	->SingleSignOutFilter
	 * 	->corsFilterConfig
	 * 	->Cas30ProxyReceivingTicketValidationFilter
	 * 	->AuthenticationFilter
	 * 	->filterWrapperRegistration
	 */
	
	/**
	 * 自定义Axios权限拦截器 - vue前端使用axios发送异步请求
	 * 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<AxiosAuthFilter> ajaxAuthFilterRegistration() {
		FilterRegistrationBean<AxiosAuthFilter> registration = new FilterRegistrationBean<AxiosAuthFilter>();
		System.out.println("************** Filter : 构造 AxiosAuthFilter" );
		registration.setFilter(new AxiosAuthFilter());
		registration.addUrlPatterns("/*");
		registration.setName("ajaxAuthFilter");
		registration.setOrder(0);

		return registration;
	}
	
	/**
	 * 添加监听器
	 * 
	 * @return
	 */
	@Bean
	public ServletListenerRegistrationBean<EventListener> singleSignOutListenerRegistration() {
		ServletListenerRegistrationBean<EventListener> registrationBean = new ServletListenerRegistrationBean<EventListener>();
		System.out.println("************** Filter : 构造 SingleSignOutHttpSessionListener");
		
		registrationBean.setListener(new SingleSignOutHttpSessionListener());
		registrationBean.setOrder(0);
		return registrationBean;
	}
	/**
	 * 配置登出过滤器
	 * 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<SingleSignOutFilter> filterSingleRegistration() {
		final FilterRegistrationBean<SingleSignOutFilter> registration = new FilterRegistrationBean<SingleSignOutFilter>();
		System.out.println("************** Filter : 构造 SingleSignOutFilter  = " + CAS_SERVER_URL_PREFIX);
		registration.setFilter(new SingleSignOutFilter());
		// 设定匹配的路径
		registration.addUrlPatterns("/*");
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("casServerUrlPrefix", CAS_SERVER_URL_PREFIX);
		registration.setInitParameters(initParameters);
		// 设定加载的顺序
		registration.setOrder(0);
		return registration;
	}
	
	/**
	 * Cros跨域
	 * 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<CorsFilterConfig> corsFilterConfig() {
		FilterRegistrationBean<CorsFilterConfig> registration = new FilterRegistrationBean<CorsFilterConfig>();
		System.out.println("************** Filter : 构造 corsFilterConfig" );
		registration.setFilter(new CorsFilterConfig());
		registration.addUrlPatterns("/*");
		registration.setName("corsFilterConfig");
		registration.setOrder(0);

		return registration;
	}
	@Bean
	public ErrorPageFilter errorPageFilter() {
		return new ErrorPageFilter();
	}
	@Bean
	public FilterRegistrationBean<ErrorPageFilter> disableSpringBootErrorFilter(ErrorPageFilter filter) {
		FilterRegistrationBean<ErrorPageFilter> filterRegistrationBean = new FilterRegistrationBean<ErrorPageFilter>();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}
	
	/**
	 * 配置过滤验证器 这里用的是Cas30ProxyReceivingTicketValidationFilter
	 * 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> filterValidationRegistration() {
		final FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> registration = new FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter>();
		registration.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
		System.out.println("************** Filter : 构造 Cas30ProxyReceivingTicketValidationFilter");
		// 设定匹配的路径
		registration.addUrlPatterns("/*");
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("casServerUrlPrefix", CAS_SERVER_URL_PREFIX);
		initParameters.put("serverName", CAS_CLIENT_HOST_URL);
		//initParameters.put("useSession", "true");
		registration.setInitParameters(initParameters);
		// 设定加载的顺序
		registration.setOrder(1);
		return registration;
	}
	
	/**
	 * 重写cas权限认证filter Bean
	 * 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<AuthenticationFilter> filterAuthenticationRegistration() {
		FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<AuthenticationFilter>();

		registration.setFilter(new AuthenticationFilter());
		System.out.println("************** Filter : 构造 AuthenticationFilter");
		// 设定匹配的路径
		registration.addUrlPatterns("/*");
		Map<String, String> initParameters = new HashMap<String, String>();

		initParameters.put("casServerLoginUrl", CAS_SERVER_LOGIN_URL);
		initParameters.put("serverName", CAS_CLIENT_HOST_URL);
		initParameters.put("ignorePattern", "/loginOut/outHandPageSuccess");// 放开不需要cas认证的url
		registration.setInitParameters(initParameters);
		// 设定加载的顺序
		registration.setOrder(2);

		return registration;
	}

	/**
	 * request wraper过滤器, 目的是将CAS server返回的信息封装到Http
	 * request里面，这样客户端就可以用request.getRemoteUser()来获取用户名等信息了
	 * 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<HttpServletRequestWrapperFilter> filterWrapperRegistration() {
		final FilterRegistrationBean<HttpServletRequestWrapperFilter> registration = new FilterRegistrationBean<HttpServletRequestWrapperFilter>();
		System.out.println("************** Filter : 构造 HttpServletRequestWrapperFilter");
		registration.setFilter(new HttpServletRequestWrapperFilter());
		// 设定匹配的路径
		registration.addUrlPatterns("/*");
		// 设定加载的顺序
		registration.setOrder(3);
		return registration;
	}

}
