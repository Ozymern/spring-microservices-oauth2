package com.ozymern.spring.microservices.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class SpringMicroservicesOauth2Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringMicroservicesOauth2Application.class, args);
    }


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public void run(String... args) throws Exception {
        System.out.println(bCryptPasswordEncoder.encode("12345"));
    }
}
