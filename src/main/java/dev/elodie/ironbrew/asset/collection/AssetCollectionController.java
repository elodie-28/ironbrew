package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.rulespackage.RulesPackageService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("expansionId", id);
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

        addAssetCollectionIdsToAttributes(
                id,
                model,
                "replacesIds",
                assetCollection.getReplaces(),
                ruleset
        );

        addAssetCollectionIdsToAttributes(
                id,
                model,
                "enhancesIds",
                assetCollection.getEnhances(),
                ruleset
        );

        return "assetcollections/form";
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
            model.addAttribute("expansionId", expansionId);
            model.addAttribute("expansionTitle", title);
            model.addAttribute("isEdit", false);

            String ruleset =  rulesPackageService.getRuleset(expansionId);

            addAssetCollectionIdsToAttributes(
                    expansionId,
                    model,
                    "replacesIds",
                    assetCollection.getReplaces(),
                    ruleset
            );

            addAssetCollectionIdsToAttributes(
                    expansionId,
                    model,
                    "enhancesIds",
                    assetCollection.getEnhances(),
                    ruleset
            );

            return "assetcollections/form";
        }

        UUID id = assetCollectionService.create(assetCollection, expansionId);
        return "redirect:/asset-collections/" + id;
    }

    @GetMapping("/asset-collections/{id}")
    public String findAssetCollectionById(@PathVariable UUID id, Model model) {
        AssetCollectionResponse assetCollection = assetCollectionService.findAssetCollectionResponseById(id);
        model.addAttribute("assetCollection", assetCollection);

        UUID expansionId = assetCollectionService.findRulesPackageIdById(id);
        model.addAttribute("expansionId", expansionId);

        String title = rulesPackageService.getExpansionTitle(expansionId);
        model.addAttribute("expansionTitle", title);

        return "assetcollections/view";
    }

    @GetMapping("/asset-collections/{id}/edit")
    public String editAssetCollection(@PathVariable UUID id, Model model) {
        AssetCollectionRequest assetCollection = assetCollectionService.findAssetCollectionRequestByRulesPackageId(id);
        model.addAttribute("assetCollection", assetCollection);

        UUID expansionId = assetCollectionService.findRulesPackageIdById(id);
        model.addAttribute("expansionId", expansionId);

        String title = rulesPackageService.getExpansionTitle(expansionId);
        model.addAttribute("expansionTitle", title);

        model.addAttribute("isEdit", true);

        String ruleset =  rulesPackageService.getRuleset(expansionId);

        addAssetCollectionIdsToAttributes(
                expansionId,
                model,
                "replacesIds",
                assetCollection.getReplaces(),
                ruleset
        );

        addAssetCollectionIdsToAttributes(
                expansionId,
                model,
                "enhancesIds",
                assetCollection.getEnhances(),
                ruleset
        );

        return "assetcollections/form";
    }

    @PostMapping("/asset-collections/{id}")
    public String updateAssetCollection(
            @PathVariable("id") UUID id,
            @Valid AssetCollectionRequest assetCollection,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("assetCollection", assetCollection);
            model.addAttribute("isEdit", true);

            UUID expansionId = assetCollectionService.findRulesPackageIdById(id);
            model.addAttribute("expansionId", expansionId);

            String title = rulesPackageService.getExpansionTitle(expansionId);
            model.addAttribute("expansionTitle", title);

            String ruleset =  rulesPackageService.getRuleset(expansionId);

            addAssetCollectionIdsToAttributes(
                    expansionId,
                    model,
                    "replacesIds",
                    assetCollection.getReplaces(),
                    ruleset
            );

            addAssetCollectionIdsToAttributes(
                    expansionId,
                    model,
                    "enhancesIds",
                    assetCollection.getEnhances(),
                    ruleset
            );

            return "assetcollections/form";
        }

        assetCollectionService.update(assetCollection, id);
        return "redirect:/asset-collections/" + id;
    }

    @PostMapping("/asset-collections/{id}/delete")
    public String deleteAssetCollection(
            @PathVariable("id") UUID id,
            RedirectAttributes redirectAttributes
    ) {
        UUID expansionId = assetCollectionService.findRulesPackageIdById(id);
        assetCollectionService.deleteById(id);
        redirectAttributes.addFlashAttribute("successDeleteMessage", "Asset collection deleted successfully");
        return "redirect:/expansions/" + expansionId + "/asset-collections";
    }

    private void addAssetCollectionIdsToAttributes(UUID expansionId, Model model, String attribute, List<String> attributeIds, String ruleset) {
        List<String> assetCollectionIds = new ArrayList<>();
        if(ruleset.equals("classic")){
            assetCollectionIds.addAll(List.of(
                    "asset_collection:classic/combat_talent",
                    "asset_collection:classic/companion",
                    "asset_collection:classic/path",
                    "asset_collection:classic/ritual"
            ));
        } else if (ruleset.equals("starforged")) {
            assetCollectionIds.addAll(List.of(
                    "asset_collection:starforged/command_vehicle",
                    "asset_collection:starforged/companion",
                    "asset_collection:starforged/deed",
                    "asset_collection:starforged/module",
                    "asset_collection:starforged/path",
                    "asset_collection:starforged/support_vehicle"
            ));
        }

        for(String attributeId: attributeIds){
            if(!assetCollectionIds.contains(attributeId)){
                assetCollectionIds.add(attributeId);
            }
        }
        model.addAttribute(attribute, assetCollectionIds);
    }
}
