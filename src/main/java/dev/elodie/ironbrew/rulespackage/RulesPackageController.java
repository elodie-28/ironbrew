package dev.elodie.ironbrew.rulespackage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.UUID;

@Controller
public class RulesPackageController {
    private final RulesPackageService rulesPackageService;

    public RulesPackageController(RulesPackageService rulesPackageService) {
        this.rulesPackageService = rulesPackageService;
    }

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
        ExpansionResponse expansion = rulesPackageService.getExpansionDtoById(id);
        model.addAttribute("expansion", expansion);
        return "expansions/view";
    }

    @PostMapping("/expansions/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        rulesPackageService.deleteById(id);
        redirectAttributes.addFlashAttribute("successDeleteMessage", "Expansion deleted successfully");
        return "redirect:/your-expansions";
    }
}
