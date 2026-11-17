package org.greet.repository;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import org.greet.entity.Greet;
import org.greet.service.GreetService;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Log4j2
@Repository
@AllArgsConstructor
public class GreetRepository {

    Map<UUID, Greet> greets;
    private Faker faker;

    @PostConstruct
    private void init() {
         greets = new ConcurrentHashMap<>();

        IntStream.range(0, 5).forEach(num -> {
            Greet greet = Greet.builder()
                    .id(UUID.randomUUID())
                    .title(faker.lorem().sentence())
                    .author(faker.name().fullName())
                    .description(faker.lorem().paragraph())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            greets.put(greet.getId(), greet);
        });

        log.info("Total greets : {}", greets.size());
    }

    public Collection<Greet> getAllGreets() {
        log.debug("Finding all greets");
        return greets.values();
    }

    public Greet findById(UUID id) {
        log.debug("Finding greet by id : {}", id);
        return greets.get(id);
    }

    public Greet save(Greet greet) {
        greets.put(greet.getId(), greet);
        return greet;
    }

    public Greet deleteById(UUID id) {
        return greets.remove(id);
    }




}
