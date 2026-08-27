package com.example.coreapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.example.coreapi",
        "com.example.reportingapi",
        "com.example.common"
})
@EntityScan(basePackages = {
        "com.example.common.entity",
        "com.example.coreapi",
        "com.example.reportingapi"
})
@EnableJpaRepositories(basePackages = {
        "com.example.common",
        "com.example.coreapi",
        "com.example.reportingapi"
})
public class CoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApiApplication.class, args);
    }

}