package dev.elodie.ironbrew.asset.collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetCollectionRepository extends JpaRepository<AssetCollection, UUID> {
    List<AssetCollectionResponse> findAllAssetCollectionResponseByRulesPackageId(UUID rulesPackageId);

    Optional<AssetCollectionResponse> findAssetCollectionResponseById(UUID id);

    @Query("SELECT rulesPackage.id FROM AssetCollection where id = :id")
    UUID findRulesPackageIdById(@Param("id") UUID id);
}
