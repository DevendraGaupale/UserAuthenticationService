package com.userauthenticationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserAuthenticationServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(UserAuthenticationServiceApplication.class, args);
        System.out.println("User Authentication Service Application Started");
    }

}
