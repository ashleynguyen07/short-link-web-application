package com.example.shortlinkapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
public class ShortLinkApplication {

  public static void main(String[] args) {
    SpringApplication.run(ShortLinkApplication.class, args);
  }
}
