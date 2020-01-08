package zqit.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.unicon.cas.client.configuration.EnableCasClient;

@SpringBootApplication
@EnableCasClient // 开启cas
public class SpringBootClientApp2 {
	public static void main(String[] args) {
		SpringApplication.run(SpringBootClientApp2.class, args);
	}
}