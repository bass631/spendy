package dev.bass631.spendy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SpendyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpendyBackendApplication.class, args);
    }
}
