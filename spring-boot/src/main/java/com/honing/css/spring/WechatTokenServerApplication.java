package com.honing.css.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WechatTokenServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(WechatTokenServerApplication.class, args);
  }
}
