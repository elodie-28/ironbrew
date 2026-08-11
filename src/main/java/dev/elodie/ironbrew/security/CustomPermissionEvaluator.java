package dev.elodie.ironbrew.security;

import dev.elodie.ironbrew.rulespackage.RulesPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final RulesPackageRepository rulesPackageRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if(targetId instanceof UUID id && targetType.equals("Expansion") && (permission.equals("read") || permission.equals("update") || permission.equals("delete"))){
            String ownerEmail = rulesPackageRepository.findOwnerEmailById(id);
            return ownerEmail.equals(authentication.getName());
        }

        return false;
    }
}
