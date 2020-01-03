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
import javax.servlet.http.HttpSession;

/**
 * post请求过期filter 
 * filter 定义迁移到 plugin/cas/CasConfig.java 管理
 * 
 * @author mac
 *
 */
public class AxiosAuthFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// 无需拦截url
		String requestUrl = req.getRequestURL().toString();
		if (requestUrl.indexOf("/login/outHand") > 0 || requestUrl.indexOf("/login/outDef") > 0) {
			filterChain.doFilter(req, resp);
		}

		//判断post-ajax请求是否缺少权限
		HttpSession session = req.getSession(false);
		String zqRequestType = req.getHeader("zq-request-type");
		if ((session == null) && ("axios".equals(zqRequestType))) {
			resp.setHeader("authCode", "401");
			resp.getWriter().append("No Access").flush();
			return ;
		}
		
		resp.setHeader("authCode", "205");
		filterChain.doFilter(req, resp);
	}

	public void init(FilterConfig arg0) throws ServletException {

	}
	
	public void destroy() {

	}

}
