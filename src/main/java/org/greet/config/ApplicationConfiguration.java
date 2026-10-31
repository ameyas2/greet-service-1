package org.greet.config;

import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class ApplicationConfiguration {

    @Bean
    public Faker faker() {
        return new Faker();
    }
}
