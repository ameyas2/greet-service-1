package org.greet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public class GreetMapper {
    /*@Mapping(target = "greetDTO", ignore = true)
    Gree toGreet(Post post);

    @Mapping(target = "user", ignore = true)
    Post toPost(PostDTO postDTO);*/
}
