package zqit.client.pilgins.springSession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
//spring使用一个拦截器来实现Session共享的操作，而配置的这个Bean，则是让Spring根据配置文件中的配置连到Redis
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800, redisNamespace = "[ss]-CAS-CLIENT")
public class RedisSessionConfig {
	
	@Bean
	public static ConfigureRedisAction configureRedisAction() {
		return ConfigureRedisAction.NO_OP;
	}
}
