package dev.elodie.ironbrew.asset.collection;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetCollectionRepository extends JpaRepository<AssetCollection, UUID> {
    List<AssetCollectionResponse> findAllAssetCollectionResponseByRulesPackageId(UUID rulesPackageId, Sort sort);

    Optional<AssetCollectionResponse> findAssetCollectionResponseById(UUID id);

    @Query("SELECT rulesPackage.id FROM AssetCollection where id = :id")
    UUID findRulesPackageIdById(@Param("id") UUID id);

    @Query("SELECT new dev.elodie.ironbrew.asset.collection.AssetCollectionRequest(name, canonicalName, color, replaces, enhances, summary, description) FROM AssetCollection where id = :id")
    Optional<AssetCollectionRequest> findAssetCollectionRequestById(UUID id);

    @Query("SELECT rulesPackage.owner.email FROM AssetCollection")
    String findOwnerEmailById(UUID id);
}
