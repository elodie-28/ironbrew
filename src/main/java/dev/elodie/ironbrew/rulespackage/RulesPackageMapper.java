package dev.elodie.ironbrew.rulespackage;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RulesPackageMapper {
    Expansion toExpansion(ExpansionRequest expansionRequest);
    void update(@MappingTarget Expansion expansion, ExpansionRequest expansionRequest);
}
