package zqit.client.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/loginOut")
public class LoginOutController {
	@Value("${cas.server-url-prefix}")
	private String CAS_SERVER_URL_PREFIX;

	/**
	 * 跳转到默认页面
	 * 
	 * @param session
	 * @return
	 */
	@GetMapping("/outDef")
	public String outDef(HttpSession session) {
		session.invalidate();
		
		// 这个是直接退出，走的是默认退出方式
		String url = CAS_SERVER_URL_PREFIX+"/logout";
		System.out.println("跳转到默认页面 : " + url);
		return "redirect:" + url;
	}

	/**
	 * 跳转到指定页面
	 * 
	 * @param session
	 * @return
	 */
	@GetMapping("/outHand")
	public String outHand(HttpServletRequest request, HttpSession session) {
		session.invalidate();
		
		// 退出登录后，跳转到退成成功的页面，不走默认页面
		int localPort = request.getLocalPort();
		String url = CAS_SERVER_URL_PREFIX+"/logout?service=http://127.0.0.1:"+localPort+"/loginOut/outHandPageSuccess";
		System.out.println("跳转到指定页面 : " + url);
		return "redirect:" + url;
	}

	// 登出跳转到指定路径
	@GetMapping("/outHandPageSuccess")
	@ResponseBody
	public String outHandPageSuccess(HttpServletRequest request) {
		int localPort = request.getLocalPort();
		return "cas outHandPageSuccess - 登出跳转到指定路径 - " + localPort;
	}

}