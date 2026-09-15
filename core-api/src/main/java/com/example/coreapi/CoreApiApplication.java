package com.example.coreapi;

import com.example.coreapi.console.ConsoleApiClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;

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
        ConfigurableApplicationContext context = null;
        try {
            context = SpringApplication.run(CoreApiApplication.class, args);
        } catch (Exception e) {
            System.out.println("[console] Spring Boot could not start (" + e.getMessage()
                    + "). Assuming an instance is already running.");
        }
        if (!Arrays.asList(args).contains("--no-menu")) {
            ConsoleApiClient.main(args);
            if (context != null) {
                System.exit(SpringApplication.exit(context, () -> 0));
            }
        }
    }

}