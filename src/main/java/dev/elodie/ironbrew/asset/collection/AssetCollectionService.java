package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.exception.NameAlreadyTakenException;
import dev.elodie.ironbrew.exception.ResourceNotFoundException;
import dev.elodie.ironbrew.rulespackage.RulesPackage;
import dev.elodie.ironbrew.rulespackage.RulesPackageRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
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
        try{
            assetCollection = assetCollectionRepository.save(assetCollection);
        } catch(DataIntegrityViolationException ex){
            if(ex.getMessage().contains(AssetCollection.UNIQUE_NAME_CONSTRAINT)){
                throw new NameAlreadyTakenException();
            }
        }
        return assetCollection.getId();
    }

    public List<AssetCollectionResponse> findAllAssetCollectionResponseByRulesPackageId(UUID rulesPackageId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        return assetCollectionRepository.findAllAssetCollectionResponseByRulesPackageId(rulesPackageId, sort);
    }

    public AssetCollectionResponse findAssetCollectionResponseById(UUID id) {
        return assetCollectionRepository.findAssetCollectionResponseById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public UUID findRulesPackageIdById(UUID id) {
        return assetCollectionRepository.findRulesPackageIdById(id);
    }

    public AssetCollectionRequest findAssetCollectionRequestByRulesPackageId(UUID id) {
        return assetCollectionRepository.findAssetCollectionRequestById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public void update(AssetCollectionRequest request, UUID id) {
        AssetCollection assetCollection = assetCollectionRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        assetCollectionMapper.update(assetCollection, request);
        try{
            assetCollectionRepository.save(assetCollection);
        } catch(DataIntegrityViolationException ex){
            if(ex.getMessage().contains(AssetCollection.UNIQUE_NAME_CONSTRAINT)){
                throw new NameAlreadyTakenException();
            }
        }

    }

    public void deleteById(UUID id) {
        assetCollectionRepository.deleteById(id);
    }
}
