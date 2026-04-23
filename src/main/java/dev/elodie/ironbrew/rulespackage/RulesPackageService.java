package dev.elodie.ironbrew.rulespackage;

import dev.elodie.ironbrew.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RulesPackageService {
    private final RulesPackageRepository rulesPackageRepository;

    public RulesPackageService(RulesPackageRepository rulesPackageRepository) {
        this.rulesPackageRepository = rulesPackageRepository;
    }

    public Page<ExpansionResponse> getExpansionDtosByOwnerEmail(String email, Pageable pageable){
        return rulesPackageRepository.findExpansionDtosByOwnerEmail(email, pageable);
    }

    public ExpansionResponse getExpansionDtoById(UUID id){
        return rulesPackageRepository.findExpansionDtoById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
