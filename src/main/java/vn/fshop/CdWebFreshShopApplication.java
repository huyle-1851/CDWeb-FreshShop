package vn.fshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "vn.fshop.repository")
@EntityScan(basePackages = "vn.fshop.model")
public class CdWebFreshShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(CdWebFreshShopApplication.class, args);
	}
}
