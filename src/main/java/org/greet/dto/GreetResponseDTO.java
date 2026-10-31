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
    private String header;
    private String description;
    private String author;
    private LocalDateTime time;
}
