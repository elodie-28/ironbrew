package dev.elodie.ironbrew.rulespackage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
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
        return "expansions/list";
    }

    @GetMapping("/expansions/{id}")
    public String view(Model model, @PathVariable UUID id) {
        ExpansionResponse expansion = rulesPackageService.getExpansionDtoById(id);
        model.addAttribute("expansion", expansion);
        return "expansions/view";
    }
}
