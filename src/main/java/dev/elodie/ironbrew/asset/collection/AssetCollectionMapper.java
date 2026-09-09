package dev.elodie.ironbrew.asset.collection;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AssetCollectionMapper {
    AssetCollection toAssetCollection(AssetCollectionRequest assetCollectionRequest);

    void update(@MappingTarget AssetCollection assetCollection, AssetCollectionRequest assetCollectionRequest);
}
