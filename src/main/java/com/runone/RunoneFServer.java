package com.runone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.servlet.MultipartConfigElement;

@ComponentScan(basePackages = "com.runone")
@EnableAutoConfiguration
public class RunoneFServer {
    public static void main(String[] args) {
        SpringApplication.run(RunoneFServer.class, args);
    }
}


