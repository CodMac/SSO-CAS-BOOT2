package zqit.server.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RESTFUL方式认证 
 * 
 * @author mac
 *
 */
@RestController
@RequestMapping("/authn")
public class RestAuthnAPI {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestAuthnAPI.class);

	/**
	 * 接口认证
	 * 1. cas 服务端会通过post请求，并且把用户信息以"用户名:密码"进行Base64编码放在authorization请求头中
	 * 2. 验证成功 -> 
	 * 		a. 返回200状态码
	 * 		b. json返回体格式需为:
	 * 			{"@class":"org.apereo.cas.authentication.principal.SimplePrincipal","id":"casuser","attributes":{}}
	 * 3. 403用户不可用；404账号不存在；423账户被锁定；412过期；428密码需要更改；其他登录失败
	 * @param httpHeaders
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("rawtypes")
	@PostMapping("/login")
	public Object login(@RequestHeader HttpHeaders httpHeaders) throws UnsupportedEncodingException {
		LOGGER.info("Rest api login.");
		LOGGER.debug("request headers: {}", httpHeaders);
	
		UserTemp userTemp = obtainUserFormHeader(httpHeaders);
		
		String username = userTemp.getUsername();
		String password = userTemp.getPassword();
		
		//返回状态码：403用户不可用；404账号不存在；423账户被锁定；412过期；428密码需要更改；其他登录失败
		if(!"rest".equals(username)) {
			//404账号不存在
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		if(!"rest".equals(password)) {
			// 密码不匹配
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		
		SysUser user = new SysUser();
		user.setUsername(username);
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("type", "restful login");
		user.setAttributes(attributes );
		return user;
	}

	/**
	 * 根据请求头获取用户名及密码
	 *
	 * @param httpHeaders
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private UserTemp obtainUserFormHeader(HttpHeaders httpHeaders) throws UnsupportedEncodingException {
		// 当请求过来时，会通过把用户信息放在请求头authorization中，并且通过Basic认证方式加密
		String authorization = httpHeaders.getFirst("authorization");// 将得到 Basic Base64(用户名:密码)
		String baseCredentials = authorization.split(" ")[1];
		String usernamePassword = new String(Base64Utils.decodeFromString(baseCredentials));
		LOGGER.debug("login user: {}", usernamePassword);
		String credentials[] = usernamePassword.split(":");
		return new UserTemp(credentials[0], credentials[1]);
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	public class UserTemp {
		private String username;
		private String password;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	public class SysUser {

	    @JsonProperty("id")
	    @NotNull
	    private String username;

	    @JsonProperty("@class")
	    //需要返回实现org.apereo.cas.authentication.principal.Principal的类名接口
	    private String clazz = "org.apereo.cas.authentication.principal.SimplePrincipal";


	    @JsonProperty("attributes")
	    private Map<String, Object> attributes = new HashMap<String, Object>();

	    @JsonIgnore
	    @NotNull
	    private String password;

	    @JsonIgnore
	    //用户是否不可用
	    private boolean disable = false;


	    @JsonIgnore
	    //用户是否过期
	    private boolean expired = false;

	    @JsonIgnore
	    //是否锁定
	    private boolean locked = false;

	    @JsonIgnore
	    public SysUser addAttribute(String key, Object val) {
	        getAttributes().put(key, val);
	        return this;
	    }
	}
	
	
}
