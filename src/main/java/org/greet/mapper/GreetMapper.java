package org.greet.mapper;

import org.greet.dto.GreetRequestDTO;
import org.greet.dto.GreetResponseDTO;
import org.greet.entity.Greet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GreetMapper {
    // Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Greet toGreet(GreetRequestDTO dto);

    // Entity -> Request DTO
    GreetRequestDTO toGreetRequestDTO(Greet greet);

    // Entity -> Response DTO
    @Mapping(source = "title", target = "header")
    @Mapping(source = "createdAt", target = "time")
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "error", ignore = true)
    GreetResponseDTO toGreetResponseDTO(Greet greet);
}
