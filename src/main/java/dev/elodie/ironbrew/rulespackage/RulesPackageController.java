package dev.elodie.ironbrew.rulespackage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class RulesPackageController {
    private final RulesPackageService rulesPackageService;

    public RulesPackageController(RulesPackageService rulesPackageService) {
        this.rulesPackageService = rulesPackageService;
    }

    @GetMapping("/your-rules-packages")
    public String yourRulesPackages(Model model, Principal principal) {
        String email = principal.getName();
        List<ExpansionDto> expansions = rulesPackageService.getExpansionDtosByOwnerEmail(email);
        model.addAttribute("expansions", expansions);
        return "your-rules-packages";
    }
}
