package zqit.server.pulgins.mybatis;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@MapperScan("zqit.server.mapper")
public class MybatisConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private Environment env;

	// 数据源
	@Bean
	public DataSource dataSource() {
		System.out.println("mybatis-dataSource 初始化");
		
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(env.getProperty("mbs.datasource.url"));
		dataSource.setUsername(env.getProperty("mbs.datasource.username"));// 用户名
		dataSource.setPassword(env.getProperty("mbs.datasource.password"));// 密码
		dataSource.setDriverClassName(env.getProperty("mbs.datasource.driver-class-name"));
		dataSource.setInitialSize(2);// 初始化时建立物理连接的个数
		dataSource.setMaxActive(20);// 最大连接池数量
		dataSource.setMinIdle(0);// 最小连接池数量
		dataSource.setMaxWait(60000);// 获取连接时最大等待时间，单位毫秒。
		dataSource.setValidationQuery("SELECT 1");// 用来检测连接是否有效的sql
		dataSource.setTestOnBorrow(false);// 申请连接时执行validationQuery检测连接是否有效
		dataSource.setTestWhileIdle(true);// 建议配置为true，不影响性能，并且保证安全性。
		dataSource.setPoolPreparedStatements(false);// 是否缓存preparedStatement，也就是PSCache
		return dataSource;
	}

	// sqlsessionfactory工厂
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		System.out.println("mybatis-sqlSessionFactory 初始化");
		
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
				.getResources(env.getProperty("mbs.mapper-locations")));
		return sessionFactory.getObject();
	}

}