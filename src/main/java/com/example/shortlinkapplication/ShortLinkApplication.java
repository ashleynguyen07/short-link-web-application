package com.example.shortlinkapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@EnableConfigurationProperties
public class ShortLinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortLinkApplication.class, args);
	}
}
