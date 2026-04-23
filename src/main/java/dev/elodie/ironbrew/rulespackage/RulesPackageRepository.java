package dev.elodie.ironbrew.rulespackage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RulesPackageRepository extends JpaRepository<RulesPackage, UUID> {
    @Query("SELECT id, title, dataswornId, description, releaseDate, url, license, ruleset FROM Expansion WHERE owner.email = :email")
    Page<ExpansionResponse> findExpansionDtosByOwnerEmail(@P("email") String email, Pageable pageable);

    @Query("SELECT id, title, dataswornId, description, releaseDate, url, license, ruleset FROM Expansion WHERE id = :id")
    Optional<ExpansionResponse> findExpansionDtoById(@P("id") UUID id);
}
