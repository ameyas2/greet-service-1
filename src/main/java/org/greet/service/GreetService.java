package org.greet.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import org.greet.dto.GreetResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
@AllArgsConstructor
public class GreetService {

    private Faker faker;

    public GreetResponseDTO greet() {
        return GreetResponseDTO.builder()
                .time(LocalDateTime.now())
                .header(faker.lorem().sentence())
                .author(faker.name().fullName())
                .description(faker.lorem().paragraph())
                .build();
    }
}
