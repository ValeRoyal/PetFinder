package com.royal.msshelter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MsShelterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsShelterApplication.class, args);
    }

}
