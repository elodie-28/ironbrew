package dev.elodie.ironbrew.rulespackage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RulesPackageRepository extends JpaRepository<RulesPackage, UUID> {
    @Query("SELECT id, title, dataswornId, description, releaseDate, url, license, ruleset FROM Expansion WHERE owner.email = :email")
    Page<ExpansionResponse> findExpansionDtosByOwnerEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT id, title, dataswornId, description, releaseDate, url, license, ruleset FROM Expansion WHERE id = :id")
    Optional<ExpansionResponse> findExpansionResponseById(@Param("id") UUID id);

    @Query("SELECT e from Expansion e where e.id = :id")
    Optional<Expansion> findExpansionById(@Param("id") UUID id);

    @Query("SELECT new dev.elodie.ironbrew.rulespackage.ExpansionRequest(title, dataswornId, description, releaseDate, url, license, ruleset) FROM Expansion WHERE id = :id")
    Optional<ExpansionRequest> findExpansionRequestById(@Param("id") UUID id);

    @Query("SELECT owner.email from Expansion where id = :id")
    String findOwnerEmailById(@Param("id") UUID id);
}
