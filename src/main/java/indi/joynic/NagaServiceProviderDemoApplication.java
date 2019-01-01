package indi.joynic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "indi.joynic")
public class NagaServiceProviderDemoApplication {

	private static final Logger logger = LoggerFactory.getLogger(NagaServiceProviderDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(NagaServiceProviderDemoApplication.class, args);

		logger.info("app started!");
	}
}
