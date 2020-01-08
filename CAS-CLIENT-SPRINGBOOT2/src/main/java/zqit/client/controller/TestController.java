package zqit.client.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {


	@GetMapping("/info")
	@ResponseBody
	public String info(HttpServletRequest request, HttpSession session) throws IOException {
		// 获得当前的登录信息
		AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
		String principalName = principal.getName();
		
		int localPort = request.getLocalPort();
		String id = session.getId();
		return "springboot-client:"+localPort+" , principalName: " + principalName + ", sessionId: " + id;
	}

}
