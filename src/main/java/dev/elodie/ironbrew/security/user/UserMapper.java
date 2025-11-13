package dev.elodie.ironbrew.security.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(
            target = "website",
            expression = "java(registerRequest.getWebsite().isEmpty() ? null : registerRequest.getWebsite())"
    )
    User toUser(RegisterRequest registerRequest);
}
