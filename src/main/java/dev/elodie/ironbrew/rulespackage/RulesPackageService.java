package dev.elodie.ironbrew.rulespackage;

import dev.elodie.ironbrew.exception.ResourceNotFoundException;
import dev.elodie.ironbrew.security.user.User;
import dev.elodie.ironbrew.security.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RulesPackageService {
    private final RulesPackageRepository rulesPackageRepository;
    private final RulesPackageMapper rulesPackageMapper;
    private final UserService userService;

    public Page<ExpansionResponse> getExpansionDtosByOwnerEmail(String email, Pageable pageable){
        return rulesPackageRepository.findExpansionDtosByOwnerEmail(email, pageable);
    }

    @PreAuthorize("hasPermission(#id, 'Expansion', 'read')")
    public ExpansionResponse getExpansionResponseById(UUID id){
        return rulesPackageRepository.findExpansionResponseById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @PreAuthorize("hasPermission(#id, 'Expansion', 'delete')")
    public void deleteById(UUID id){
        rulesPackageRepository.deleteById(id);
    }

    public UUID create(ExpansionRequest expansionRequest, String ownerEmail){
        Expansion expansion = rulesPackageMapper.toExpansion(expansionRequest);
        User owner = userService.findByEmail(ownerEmail);
        expansion.setOwner(owner);
        rulesPackageRepository.save(expansion);
        return expansion.getId();
    }

    @PreAuthorize("hasPermission(#id, 'Expansion', 'update')")
    public void update(UUID id, ExpansionRequest expansionRequest){
        Expansion expansion = rulesPackageRepository.findExpansionById(id).orElseThrow(ResourceNotFoundException::new);
        rulesPackageMapper.update(expansion, expansionRequest);
        rulesPackageRepository.save(expansion);
    }

    public ExpansionRequest getExpansionRequestById(UUID id){
        return rulesPackageRepository.findExpansionRequestById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public String getExpansionTitle(UUID id){
        return rulesPackageRepository.findTitleById(id);
    }
}
