package com.royal.msnotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MsNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsNotificationApplication.class, args);
    }

}
