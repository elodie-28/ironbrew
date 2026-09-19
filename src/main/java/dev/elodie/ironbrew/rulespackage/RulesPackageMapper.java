package dev.elodie.ironbrew.rulespackage;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RulesPackageMapper {
    Expansion toExpansion(ExpansionCreateRequest expansionRequest);
    void update(@MappingTarget Expansion expansion, ExpansionUpdateRequest expansionRequest);
}
