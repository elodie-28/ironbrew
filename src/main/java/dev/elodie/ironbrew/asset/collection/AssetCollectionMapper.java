package dev.elodie.ironbrew.asset.collection;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AssetCollectionMapper {
    AssetCollection toAssetCollection(AssetCollectionRequest assetCollectionRequest);
}
