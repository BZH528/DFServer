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

//    @Bean
//    MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setLocation("E:\\javapro\\RunoneFS\\DFServer\\tmp");
//        return factory.createMultipartConfig();
//    }
}


