package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.rulespackage.RulesPackageService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/expansions/{id}/asset-collections/new")
    public String newAssetCollection(@PathVariable UUID id, Model model) {
        model.addAttribute("expansionId", id);

        AssetCollectionRequest assetCollection = new AssetCollectionRequest();
        model.addAttribute("assetCollection", assetCollection);

        model.addAttribute("isEdit", false);

        String title = rulesPackageService.getExpansionTitle(id);
        model.addAttribute("expansionTitle", title);

        String ruleset =  rulesPackageService.getRuleset(id);
        List<String> assetCollectionIds = new ArrayList<>();
        if(ruleset.equals("classic")){
            assetCollectionIds = List.of(
                    "asset_collection:classic/combat_talent",
                    "asset_collection:classic/companion",
                    "asset_collection:classic/path",
                    "asset_collection:classic/ritual"
            );
        } else if (ruleset.equals("starforged")) {
            assetCollectionIds = List.of(
                    "asset_collection:starforged/command_vehicle",
                    "asset_collection:starforged/companion",
                    "asset_collection:starforged/deed",
                    "asset_collection:starforged/module",
                    "asset_collection:starforged/path",
                    "asset_collection:starforged/support_vehicle"
            );
        }
        model.addAttribute("assetCollectionIds", assetCollectionIds);

        return "assetcollections/form";
    }

    @HxRequest
    @PostMapping("/asset-collections/add-replaces-row")
    public String addReplacesRow(
            @ModelAttribute("assetCollection") AssetCollectionRequest assetCollection,
            Model model
    ) {
        assetCollection.getReplaces().add("");
        model.addAttribute("assetCollection", assetCollection);
        return "assetcollections/form :: replaces-list";
    }

    @HxRequest
    @PostMapping("/asset-collections/remove-replaces-row")
    public String removeReplacesRow(
            @RequestParam("index") int index,
            AssetCollectionRequest assetCollection,
            Model model
    ) {
        if (index >= 0 && index < assetCollection.getReplaces().size()) {
            assetCollection.getReplaces().remove(index);
        }
        model.addAttribute("assetCollection", assetCollection);
        return "assetcollections/form :: replaces-list";
    }

    @HxRequest
    @PostMapping("/asset-collections/add-enhances-row")
    public String addEnhancesRow(
            @ModelAttribute("assetCollection") AssetCollectionRequest assetCollection,
            Model model
    ) {
        assetCollection.getEnhances().add("");
        model.addAttribute("assetCollection", assetCollection);
        return "assetcollections/form :: enhances-list";
    }

    @HxRequest
    @PostMapping("/asset-collections/remove-enhances-row")
    public String removeEnhancesRow(
            @RequestParam("index") int index,
            AssetCollectionRequest assetCollection,
            Model model
    ) {
        if (index >= 0 && index < assetCollection.getEnhances().size()) {
            assetCollection.getEnhances().remove(index);
        }
        model.addAttribute("assetCollection", assetCollection);
        return "assetcollections/form :: enhances-list";
    }

    @PostMapping("/expansions/{id}/asset-collections")
    public String saveAssetCollection(
            @PathVariable("id") UUID expansionId,
            @ModelAttribute("assetCollection") @Valid AssetCollectionRequest assetCollection,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            String title  = rulesPackageService.getExpansionTitle(expansionId);
            model.addAttribute("expansionTitle", title);
            model.addAttribute("isEdit", false);
            return "assetcollections/form";
        }

        UUID id = assetCollectionService.create(assetCollection, expansionId);
        return "redirect:/asset-collections/" + id;
    }
}
