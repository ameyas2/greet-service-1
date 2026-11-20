package org.greet.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GreetResponseDTO {
    private String id;
    private String header;
    private String description;
    private String author;
    private LocalDateTime time;
    private String message;
    private String error;
}
