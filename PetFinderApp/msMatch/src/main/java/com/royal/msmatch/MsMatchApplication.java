package com.royal.msmatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MsMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsMatchApplication.class, args);
    }

}
