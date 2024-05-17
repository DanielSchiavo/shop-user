package br.com.danielschiavo.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients(basePackages = "br.com.danielschiavo")
@ComponentScan(basePackages = "br.com.danielschiavo")
@EntityScan("br.com.danielschiavo.shop.model")
@PropertySource("classpath:application-${spring.profiles.active}.properties")
@EnableJpaRepositories("br.com.danielschiavo")
public class ShopUserApplication extends SpringBootServletInitializer {
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ShopUserApplication.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(ShopUserApplication.class, args);
	}

}
