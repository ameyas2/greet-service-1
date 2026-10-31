package org.greet.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.greet.dto.GreetResponseDTO;
import org.greet.service.GreetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/greet")
@AllArgsConstructor
public class GreetController {

    private GreetService greetService;

    @GetMapping("")
    public ResponseEntity<GreetResponseDTO> greet() {
        return ResponseEntity.ok(greetService.greet());
    }
}
