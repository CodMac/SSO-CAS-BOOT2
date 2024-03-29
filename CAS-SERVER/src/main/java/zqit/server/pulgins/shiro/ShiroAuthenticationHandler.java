package zqit.server.pulgins.shiro;

import java.security.GeneralSecurityException;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.RememberMeUsernamePasswordCredential;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;

import zqit.server.entity.SysUser;
import zqit.server.mapper.SysUserMapper;

/**
 * 自定义验证器
 * @author mac
 *
 */
public class ShiroAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
	

	@Autowired
	private SysUserMapper sysUserMapper;

	public ShiroAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
			Integer order) {
		super(name, servicesManager, principalFactory, order);
	}

	@Override
	protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
			UsernamePasswordCredential transformedCredential, 
			String originalPassword) throws GeneralSecurityException {

		try {
			UsernamePasswordToken token = new UsernamePasswordToken(transformedCredential.getUsername(), transformedCredential.getPassword());

			if (transformedCredential instanceof RememberMeUsernamePasswordCredential) {
				token.setRememberMe(RememberMeUsernamePasswordCredential.class.cast(transformedCredential).isRememberMe());
			}

			Subject currentUser = getCurrentExecutingSubject();
			
			//检测是否登录 - 委托给shiroRealm 
			currentUser.login(token);
			
			String username = currentUser.getPrincipal().toString();
			SysUser one = new SysUser();
			one.setUsername(username);
			SysUser user = sysUserMapper.selectOne(one);
			
			//检测角色和权限
			checkSubjectRolesAndPermissions(user);

			//返回需要的当前登录用户信息到 Principal，客户端可以通过 request.getUserPrincipal(); 获取
			AuthenticationHandlerExecutionResult subjectResult = createAuthenticatedSubjectResult(transformedCredential, user);
			
			return subjectResult;
			
		} catch (final UnknownAccountException uae) {
			throw new AccountNotFoundException(uae.getMessage());
		} catch (final IncorrectCredentialsException ice) {
			throw new FailedLoginException(ice.getMessage());
		} catch (final LockedAccountException | ExcessiveAttemptsException lae) {
			throw new AccountLockedException(lae.getMessage());
		} catch (final ExpiredCredentialsException eae) {
			throw new CredentialExpiredException(eae.getMessage());
		} catch (final DisabledAccountException eae) {
			throw new AccountDisabledException(eae.getMessage());
		} catch (final AuthenticationException e) {
			throw new FailedLoginException(e.getMessage());
		} catch (final JsonProcessingException e) {
			throw new FailedLoginException("user json 解析失败");
		}
		
	}

	/**
	 * 检测角色和权限
	 *
	 * @param currentUser
	 *            the current user
	 * @throws FailedLoginException
	 *             the failed login exception in case roles or permissions are
	 *             absent
	 */
	protected void checkSubjectRolesAndPermissions(SysUser user) throws FailedLoginException {
		
		if(user == null){
			// 否则抛出异常,也可以自定义异常,返回不同的提示
			throw new FailedLoginException();
		}
		
//		String username = user.getUsername();
		
		//检测是否存在角色/权限
		Boolean havePermission = true; 
		if(!havePermission){
			// 否则抛出异常,也可以自定义异常,返回不同的提示
			throw new FailedLoginException();
		}else{
			return;	
		}
			
		
	}

	/**
	 * 返回需要的当前登录用户信息到 Principal
	 *
	 * @param credential
	 *            the credential
	 * @param currentUser
	 *            the current user
	 * @return the handler result
	 * @throws JsonProcessingException 
	 */
	protected AuthenticationHandlerExecutionResult createAuthenticatedSubjectResult(final Credential credential,
			SysUser user) throws JsonProcessingException {
		
		//自定义的返回用户信息 - json
//		String userJson = JacksonUtil.defaultInstance().pojo2json(user);
//		Principal principal = this.principalFactory.createPrincipal(userJson);
		//自定义的返回用户信息 - username
		Principal principal = this.principalFactory.createPrincipal(user.getUsername());
		
		return createHandlerResult(credential, principal);
	}

	/**
	 * Gets current executing subject.
	 *
	 * @return the current executing subject
	 */
	protected Subject getCurrentExecutingSubject() {
		return SecurityUtils.getSubject();
	}
}
