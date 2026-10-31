package org.greet.entity;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Greet {
    private String header;
    private String description;
    private String author;
    private LocalDateTime time;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
