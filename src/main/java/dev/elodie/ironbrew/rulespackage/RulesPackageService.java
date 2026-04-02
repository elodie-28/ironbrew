package dev.elodie.ironbrew.rulespackage;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RulesPackageService {
    private final RulesPackageRepository rulesPackageRepository;

    public RulesPackageService(RulesPackageRepository rulesPackageRepository) {
        this.rulesPackageRepository = rulesPackageRepository;
    }

    public List<ExpansionDto> getExpansionDtosByOwnerEmail(String email){
        return rulesPackageRepository.findExpansionDtosByOwnerEmail(email);
    }
}
