package org.greet;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.util.TimeZone;

@Log4j2
@SpringBootApplication
public class SpringBootInitializer {
    public static void main(String[] args) {
        try {
            // JVM default Asia/Calcutta is rejected by PostgreSQL; use IANA name Asia/Kolkata
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
            System.setProperty("user.timezone", "Asia/Kolkata");
            System.setProperty("hostName", InetAddress.getLocalHost().getHostName());
            System.setProperty("hostAddress", InetAddress.getLocalHost().getHostAddress());
            SpringApplication.run(SpringBootInitializer.class);
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            log.error("Exception : {} | root cause : {}", e.getMessage(), root.toString(), e);
        }
    }
}
