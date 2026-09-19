package dev.elodie.ironbrew.rulespackage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class RulesPackageController {
    private final RulesPackageService rulesPackageService;

    @GetMapping("/your-expansions")
    public String list(Model model, Principal principal, @RequestParam(defaultValue = "1") int page) {
        String email = principal.getName();
        Pageable pageable = PageRequest.of(page-1, 10, Sort.by("releaseDate").descending());
        Page<ExpansionResponse> result = rulesPackageService.getExpansionDtosByOwnerEmail(email, pageable);
        model.addAttribute("expansions", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        return "expansions/index";
    }

    @GetMapping("/expansions/{id}")
    public String view(Model model, @PathVariable UUID id) {
        ExpansionResponse expansion = rulesPackageService.getExpansionResponseById(id);
        model.addAttribute("expansion", expansion);
        return "expansions/view";
    }

    @PostMapping("/expansions/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        rulesPackageService.deleteById(id);
        redirectAttributes.addFlashAttribute("successDeleteMessage", "Expansion deleted successfully");
        return "redirect:/your-expansions";
    }

    @GetMapping("/expansions/{id}/edit")
    public String edit(
            @PathVariable UUID id,
            Model model
    ) {
        ExpansionUpdateRequest expansion = rulesPackageService.getExpansionRequestById(id);
        model.addAttribute("expansion", expansion);
        model.addAttribute("isEdit", true);
        model.addAttribute("id", id);
        return "expansions/form";
    }

    @PostMapping("/expansions/{id}")
    public String save(
            @ModelAttribute("expansion") @Valid ExpansionUpdateRequest expansion,
            BindingResult bindingResult,
            @PathVariable UUID id,
            Model model
    ){
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "expansions/form";
        }
        rulesPackageService.update(id, expansion);
        return "redirect:/expansions/"+id;
    }

    @GetMapping("/expansions/new")
    public String newExpansion(Model model) {
        ExpansionCreateRequest expansion = new ExpansionCreateRequest();
        model.addAttribute("expansion", expansion);
        model.addAttribute("isEdit", false);
        return "expansions/form";
    }

    @PostMapping("/expansions")
    public String create(
            @ModelAttribute("expansion") @Valid ExpansionCreateRequest expansion,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ){
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "expansions/form";
        }
        String email = principal.getName();
        UUID id = rulesPackageService.create(expansion, email);
        return "redirect:/expansions/"+id;
    }
}
