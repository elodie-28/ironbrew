package dev.elodie.ironbrew.security;

import dev.elodie.ironbrew.asset.collection.AssetCollectionRepository;
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
    private final AssetCollectionRepository assetCollectionRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if(targetId instanceof UUID id && (permission.equals("read") || permission.equals("update") || permission.equals("delete"))){
            String ownerEmail = switch (targetType) {
                case "Expansion" -> rulesPackageRepository.findOwnerEmailById(id);
                case "AssetCollection" -> assetCollectionRepository.findOwnerEmailById(id);
                default -> null;
            };
            return ownerEmail != null && ownerEmail.equals(authentication.getName());
        }

        return false;
    }
}
