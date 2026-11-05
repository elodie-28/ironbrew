package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.exception.NameAlreadyTakenException;
import dev.elodie.ironbrew.exception.ResourceNotFoundException;
import dev.elodie.ironbrew.rulespackage.RulesPackage;
import dev.elodie.ironbrew.rulespackage.RulesPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetCollectionService {
    private final AssetCollectionRepository assetCollectionRepository;
    private final AssetCollectionMapper assetCollectionMapper;
    private final RulesPackageRepository rulesPackageRepository;

    @PreAuthorize("hasPermission(#rulesPackageId, 'Expansion', 'update')")
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

    @PreAuthorize("hasPermission(#rulesPackageId, 'Expansion', 'read')")
    public List<AssetCollectionResponse> findAllAssetCollectionResponseByRulesPackageId(UUID rulesPackageId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        return assetCollectionRepository.findAllAssetCollectionResponseByRulesPackageId(rulesPackageId, sort);
    }

    @PreAuthorize("hasPermission(#id, 'AssetCollection', 'read')")
    public AssetCollectionResponse findAssetCollectionResponseById(UUID id) {
        return assetCollectionRepository.findAssetCollectionResponseById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @PreAuthorize("hasPermission(#id, 'AssetCollection', 'read')")
    public UUID findRulesPackageIdById(UUID id) {
        return assetCollectionRepository.findRulesPackageIdById(id);
    }

    @PreAuthorize("hasPermission(#id, 'AssetCollection', 'update')")
    public AssetCollectionRequest findAssetCollectionRequestById(UUID id) {
        return assetCollectionRepository.findAssetCollectionRequestById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @PreAuthorize("hasPermission(#id, 'AssetCollection', 'update')")
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

    @PreAuthorize("hasPermission(#id, 'AssetCollection', 'delete')")
    public void deleteById(UUID id) {
        assetCollectionRepository.deleteById(id);
    }
}
