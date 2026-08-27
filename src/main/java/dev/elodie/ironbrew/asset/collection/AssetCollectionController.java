package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.rulespackage.RulesPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AssetCollectionController {
    private final AssetCollectionService assetCollectionService;
    private final RulesPackageService rulesPackageService;

    @GetMapping("/expansions/{id}/asset-collections")
    public String assetCollections(@PathVariable UUID id, Model model) {
        List<AssetCollectionResponse> assetCollections = assetCollectionService.findAllAssetCollectionResponseByRulesPackageId(id);
        String title = rulesPackageService.getExpansionTitle(id);
        model.addAttribute("assetCollections", assetCollections);
        model.addAttribute("expansionTitle", title);
        model.addAttribute("rulesPackageId", id);
        return "assetcollections/index";
    }
}
