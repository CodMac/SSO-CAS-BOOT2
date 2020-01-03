package zqit.server.pulgins.shiro;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import zqit.server.pulgins.shiro.matcher.CredentialsMatcher;
import zqit.server.pulgins.shiro.realm.ShiroRealm;


/**
 * @description: shiro配置
 */
@Configuration
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class ShiroAuthenticationConfiguration  implements AuthenticationEventExecutionPlanConfigurer {

    @Bean
    public CredentialsMatcher credentialsMatcher(){
    	return new CredentialsMatcher();
    }

    @Bean
    public ShiroRealm shiroRealm(CredentialsMatcher credentialsMatcher){
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCachingEnabled(false);
        //启用身份验证缓存，即缓存AuthenticationInfo信息，默认false
        shiroRealm.setAuthenticationCachingEnabled(false);
        //启用授权缓存，即缓存AuthorizationInfo信息，默认false
        shiroRealm.setAuthorizationCachingEnabled(false);
        //设置自定义密码校验器
        shiroRealm.setCredentialsMatcher(credentialsMatcher);
        return shiroRealm;
    }
    
    @Bean(name="securityManager")
    public SecurityManager securityManager(ShiroRealm shiroRealm) {
        DefaultSecurityManager securityManager =  new DefaultSecurityManager();
        //设置自定义realm.
        securityManager.setRealm(shiroRealm);
        return securityManager;
    }
    
    /**
     * Spring静态注入
     * @return
     */
    @Bean
    public MethodInvokingFactoryBean getMethodInvokingFactoryBean(SecurityManager securityManager){
        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(new Object[]{securityManager});
        return factoryBean;
    }


    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;
    @Bean("shiroAuthenticationHandler")
    public AuthenticationHandler shiroAuthenticationHandler() {
        ShiroAuthenticationHandler handler = new ShiroAuthenticationHandler(ShiroAuthenticationHandler.class.getSimpleName(), servicesManager, new DefaultPrincipalFactory(),10);
        return handler;
    }

    @Autowired
    @Qualifier("shiroAuthenticationHandler")
    private AuthenticationHandler shiroAuthenticationHandler;
    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(shiroAuthenticationHandler);
    }

}
