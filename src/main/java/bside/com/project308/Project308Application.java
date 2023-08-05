package bside.com.project308;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = OAuth2ClientAutoConfiguration.class)
@EnableJpaAuditing
public class Project308Application {

	public static void main(String[] args) {
		SpringApplication.run(Project308Application.class, args);
	}

}
