package dev.elodie.ironbrew.security.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(source = "name", target = "name")
    User toUser(RegisterRequest registerRequest);

    @Mapping(target = "email", ignore = true)
    void updateFromAccountDto(AccountDto accountDto, @MappingTarget User user);
}
