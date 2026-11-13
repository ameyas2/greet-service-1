package org.greet.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class GreetRequestDTO {
    private String title;
    private String description;
    private String author;
}
