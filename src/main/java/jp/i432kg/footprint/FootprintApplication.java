package jp.i432kg.footprint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FootprintApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootprintApplication.class, args);
	}

}
