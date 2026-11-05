package dev.elodie.ironbrew.asset.collection;

import dev.elodie.ironbrew.exception.NameAlreadyTakenException;
import dev.elodie.ironbrew.rulespackage.RulesPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public String list(@PathVariable UUID id, Model model) {
        List<AssetCollectionResponse> assetCollections = assetCollectionService.findAllAssetCollectionResponseByRulesPackageId(id);
        String title = rulesPackageService.getExpansionTitle(id);
        model.addAttribute("assetCollections", assetCollections);
        model.addAttribute("expansionTitle", title);
        model.addAttribute("expansionId", id);
        return "assetcollections/index";
    }

    @GetMapping("/expansions/{id}/asset-collections/new")
    public String newAssetCollection(@PathVariable UUID id, Model model) {
        AssetCollectionRequest assetCollection = new AssetCollectionRequest();

        return addAttributesAndReturnForm(id, assetCollection, model, false);
    }

    @PostMapping("/expansions/{id}/asset-collections")
    public String create(
            @PathVariable("id") UUID expansionId,
            @ModelAttribute("assetCollection") @Valid AssetCollectionRequest assetCollection,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return addAttributesAndReturnForm(expansionId, assetCollection, model, false);
        }

        UUID id;
        try{
            id = assetCollectionService.create(assetCollection, expansionId);
        } catch (NameAlreadyTakenException e){
            bindingResult.rejectValue("name", "nameAlreadyTaken", "The asset collection name must be unique within an expansion.");

            return addAttributesAndReturnForm(expansionId, assetCollection, model, false);
        }
        return "redirect:/asset-collections/" + id;
    }

    @GetMapping("/asset-collections/{id}")
    public String view(@PathVariable UUID id, Model model) {
        AssetCollectionResponse assetCollection = assetCollectionService.findAssetCollectionResponseById(id);
        model.addAttribute("assetCollection", assetCollection);

        UUID expansionId = assetCollectionService.findRulesPackageIdById(id);
        model.addAttribute("expansionId", expansionId);

        String title = rulesPackageService.getExpansionTitle(expansionId);
        model.addAttribute("expansionTitle", title);

        return "assetcollections/view";
    }

    @GetMapping("/asset-collections/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        AssetCollectionRequest assetCollection = assetCollectionService.findAssetCollectionRequestById(id);

        UUID expansionId = assetCollectionService.findRulesPackageIdById(id);

        return addAttributesAndReturnForm(expansionId, assetCollection, model, true);
    }

    @PostMapping("/asset-collections/{id}")
    public String save(
            @PathVariable("id") UUID id,
            @ModelAttribute("assetCollection") @Valid AssetCollectionRequest assetCollection,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            UUID expansionId = assetCollectionService.findRulesPackageIdById(id);

            return addAttributesAndReturnForm(expansionId, assetCollection, model, true);
        }

        try{
            assetCollectionService.update(assetCollection, id);
        } catch (NameAlreadyTakenException e){
            bindingResult.rejectValue("name", "nameAlreadyTaken", "The asset collection name must be unique within an expansion.");
            UUID expansionId = assetCollectionService.findRulesPackageIdById(id);

            return addAttributesAndReturnForm(expansionId, assetCollection, model, true);
        }

        return "redirect:/asset-collections/" + id;
    }

    @PostMapping("/asset-collections/{id}/delete")
    public String delete(
            @PathVariable("id") UUID id,
            RedirectAttributes redirectAttributes
    ) {
        UUID expansionId = assetCollectionService.findRulesPackageIdById(id);
        assetCollectionService.deleteById(id);
        redirectAttributes.addFlashAttribute("successDeleteMessage", "Asset collection deleted successfully");
        return "redirect:/expansions/" + expansionId + "/asset-collections";
    }

    private void addAssetCollectionIdsToAttributes(Model model, String attribute, List<String> attributeIds, String ruleset) {
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

    private String addAttributesAndReturnForm(UUID expansionId, AssetCollectionRequest assetCollection, Model model, boolean isEdit) {
        model.addAttribute("assetCollection", assetCollection);

        model.addAttribute("isEdit", isEdit);

        String title  = rulesPackageService.getExpansionTitle(expansionId);
        model.addAttribute("expansionId", expansionId);
        model.addAttribute("expansionTitle", title);

        String ruleset =  rulesPackageService.getRuleset(expansionId);

        addAssetCollectionIdsToAttributes(
                model,
                "replacesIds",
                assetCollection.getReplaces(),
                ruleset
        );

        addAssetCollectionIdsToAttributes(
                model,
                "enhancesIds",
                assetCollection.getEnhances(),
                ruleset
        );

        return "assetcollections/form";
    }
}
