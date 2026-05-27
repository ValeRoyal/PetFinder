package com.royal.mspet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MsAdoptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAdoptionApplication.class, args);
    }

}
