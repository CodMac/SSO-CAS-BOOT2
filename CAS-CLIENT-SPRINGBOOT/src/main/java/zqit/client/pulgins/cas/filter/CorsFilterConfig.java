package zqit.client.pulgins.cas.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 跨域filter 
 * filter 定义迁移到 plugin/cas/CasConfig.java 管理
 * 
 * @author mac
 *
 */
public class CorsFilterConfig implements Filter {

	public void doFilter(ServletRequest servletResquest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		 HttpServletRequest req = (HttpServletRequest)servletResquest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
		resp.setHeader("Access-Control-Max-Age", "1728000");
		resp.setHeader("Access-Control-Allow-Headers",
				"Authentication, Authorization, content-type, Accept, x-requested-with, Cache-Control");
		filterChain.doFilter(req, resp);
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void destroy() {

	}

}
