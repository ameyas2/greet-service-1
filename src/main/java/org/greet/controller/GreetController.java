package org.greet.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.greet.dto.GreetRequestDTO;
import org.greet.dto.GreetResponseDTO;
import org.greet.entity.Greet;
import org.greet.service.GreetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/api/greet")
@AllArgsConstructor
public class GreetController {

    private GreetService greetService;

    @GetMapping("")
    public ResponseEntity<Collection<Greet>> getAll() {
        return ResponseEntity.ok(greetService.getAllGreets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Greet> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(greetService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Greet> deleteById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(greetService.delete(id));
    }

    @PostMapping("")
    public ResponseEntity<Greet> create(@RequestBody GreetRequestDTO greetRequestDTO) {
        return ResponseEntity.ok(greetService.save(greetRequestDTO));
    }

    @PutMapping("")
    public ResponseEntity<Greet> update(@RequestBody Greet greet) {
        return ResponseEntity.ok(greetService.update(greet));
    }
}
