package org.greet.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Greet {
    private UUID id;
    private String title;
    private String description;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
