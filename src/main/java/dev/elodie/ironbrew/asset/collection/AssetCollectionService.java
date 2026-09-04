package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.exception.ResourceNotFoundException;
import dev.elodie.ironbrew.rulespackage.RulesPackage;
import dev.elodie.ironbrew.rulespackage.RulesPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetCollectionService {
    private final AssetCollectionRepository assetCollectionRepository;
    private final AssetCollectionMapper assetCollectionMapper;
    private final RulesPackageRepository rulesPackageRepository;

    public UUID create(AssetCollectionRequest assetCollectionRequest, UUID rulesPackageId) {
        AssetCollection assetCollection = assetCollectionMapper.toAssetCollection(assetCollectionRequest);
        RulesPackage rulesPackage = rulesPackageRepository.getReferenceById(rulesPackageId);
        assetCollection.setRulesPackage(rulesPackage);
        return assetCollectionRepository.save(assetCollection).getId();
    }

    public List<AssetCollectionResponse> findAllAssetCollectionResponseByRulesPackageId(UUID rulesPackageId) {
        return assetCollectionRepository.findAllAssetCollectionResponseByRulesPackageId(rulesPackageId);
    }

    public AssetCollectionResponse findAssetCollectionResponseById(UUID id) {
        return assetCollectionRepository.findAssetCollectionResponseById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public UUID findRulesPackageIdById(UUID id) {
        return assetCollectionRepository.findRulesPackageIdById(id);
    }
}
