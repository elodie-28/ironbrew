package dev.elodie.ironbrew.rulespackage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RulesPackageRepository extends JpaRepository<RulesPackage, UUID> {
    @Query("SELECT id, title, dataswornId, description, date, url, license, ruleset FROM Expansion WHERE owner.email = :email")
    List<ExpansionDto> findExpansionDtosByOwnerEmail(@P("email") String email);
}
