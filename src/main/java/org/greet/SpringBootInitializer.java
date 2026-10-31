package org.greet;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;

@Log4j2
@SpringBootApplication
public class SpringBootInitializer {
    public static void main(String[] args) {
        try {
            System.setProperty("hostName", InetAddress.getLocalHost().getHostName());
            System.setProperty("hostAddress", InetAddress.getLocalHost().getHostAddress());
            SpringApplication.run(SpringBootInitializer.class);
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage(), e);
        }
    }
}
