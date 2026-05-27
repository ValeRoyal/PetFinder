package com.royal.msadopter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MsAdopterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAdopterApplication.class, args);
    }

}
