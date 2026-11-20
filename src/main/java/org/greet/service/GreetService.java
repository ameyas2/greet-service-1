package org.greet.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import org.greet.dto.GreetRequestDTO;
import org.greet.dto.GreetResponseDTO;
import org.greet.entity.Greet;
import org.greet.mapper.GreetMapper;
import org.greet.repository.GreetJpaRepository;
import org.greet.repository.GreetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@Log4j2
@AllArgsConstructor
public class GreetService {

    private Faker faker;
    private GreetRepository greetRepository;
    private GreetJpaRepository greetJpaRepository;
    private GreetMapper greetMapper;

    @PostConstruct
    private void init() {
        log.info("calling init function");

        // Skip seeding when data already exists so restarts do not keep inserting rows
        if (totalGreets() > 0) {
            log.info("Greets already present ({}), skipping seed", totalGreets());
            return;
        }

        IntStream.range(0, 5).forEach(num -> {
            // Do not set id manually: entity uses @GeneratedValue.
            // A pre-set id makes Spring Data call merge(), which throws
            // StaleObjectStateException when the row does not exist yet.
            Greet greet = Greet.builder()
                    .title(faker.lorem().sentence())
                    .author(faker.name().fullName())
                    .description(faker.lorem().paragraph())
                    .build();
            save(greet);
        });

        log.info("Total greets : {}", totalGreets());
    }


    public List<GreetResponseDTO> getAllGreets() {
        return greetJpaRepository.findAll()
                .stream()
                .map(greet -> greetMapper.toGreetResponseDTO(greet))
                .toList();
    }

    public GreetResponseDTO findById(UUID id) {
        log.info("Finding greet by id : {}", id);
        return greetMapper.toGreetResponseDTO(findGreetById(id).orElseThrow());
    }

    public GreetResponseDTO delete(UUID id) {
        log.debug("Deleting by id: {}", id);
        Greet greet = findGreetById(id).orElseThrow();
        greetJpaRepository.deleteById(id);
        GreetResponseDTO responseDTO = greetMapper.toGreetResponseDTO(greet);
        responseDTO.setMessage("Deleted Successfully : " + greet.getTitle());
        return greetMapper.toGreetResponseDTO(greet);
    }

    public GreetResponseDTO save(GreetRequestDTO greetRequestDTO) {
        Greet greet = Greet.builder()
                .title(greetRequestDTO.getTitle())
                .description(greetRequestDTO.getDescription())
                .author(greetRequestDTO.getAuthor())
                .build();
        greet = save(greet);
        log.info("Saving greet : {}", greet.getId());
        return greetMapper.toGreetResponseDTO(greet);
    }

    public GreetResponseDTO update(Greet newGreet) {
        log.info("Updating greet : {}", newGreet);
        Greet currentGreet = greetRepository.findById(newGreet.getId());
        if(currentGreet == null) {
            throw new NoSuchElementException("Greet not available for id: " + newGreet.getId());
        }
        currentGreet.setTitle(newGreet.getTitle());
        currentGreet.setAuthor(newGreet.getAuthor());
        currentGreet.setDescription(newGreet.getDescription());
        Greet greet = greetJpaRepository.save(currentGreet);
        return greetMapper.toGreetResponseDTO(greet);
    }

    private Optional<Greet> findGreetById(UUID id) {
        return greetJpaRepository.findById(id);
    }

    private Greet save(Greet greet) {
        return greetJpaRepository.save(greet);
    }

    private long totalGreets() {
        return greetJpaRepository.count();
    }
}
