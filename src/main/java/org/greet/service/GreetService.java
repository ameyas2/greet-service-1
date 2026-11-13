package org.greet.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import org.greet.dto.GreetResponseDTO;
import org.greet.entity.Greet;
import org.greet.repository.GreetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor
public class GreetService {

    private Faker faker;
    private GreetRepository greetRepository;

    public Collection<Greet> getAllGreets() {
        return greetRepository.getAllGreets();
    }

    public Greet findById(UUID id) {
        log.info("Finding greet by id : {}", id);
        return greetRepository.findById(id);
    }

    public Greet delete(UUID id) {
        log.debug("Deleting by id: {}", id);
        return greetRepository.deleteById(id);
    }

    public Greet save(Greet greet) {
        greet.setId(UUID.randomUUID());
        greet.setCreatedAt(LocalDateTime.now());
        greet.setUpdatedAt(LocalDateTime.now());
        return greetRepository.save(greet);
    }

    public Greet update(Greet newGreet) {
        log.info("Updating greet with id : {}", newGreet.getId());
        Greet currentGreet = greetRepository.findById(newGreet.getId());
        if(currentGreet == null) {
            throw new NoSuchElementException("Greet not available for id: " + newGreet.getId());
        }
        currentGreet.setTitle(newGreet.getTitle());
        currentGreet.setAuthor(newGreet.getAuthor());
        currentGreet.setDescription(newGreet.getDescription());
        return greetRepository.save(currentGreet);
    }




    /*public GreetResponseDTO get() {
        return GreetResponseDTO.builder()
                .time(LocalDateTime.now())
                .header(faker.lorem().sentence())
                .author(faker.name().fullName())
                .description(faker.lorem().paragraph())
                .build();
    }*/

}
