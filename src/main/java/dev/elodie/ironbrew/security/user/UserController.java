package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.exception.EmailAlreadyTakenException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/sign-up")
    public String signUp(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(
            @ModelAttribute @Valid RegisterRequest registerRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "sign-up";
        }
        try{
            userService.register(registerRequest);
        }catch (EmailAlreadyTakenException e){
            bindingResult.rejectValue("email", "emailAlreadyTaken", "Email is already taken");
            return "sign-up";
        }

        return "redirect:/?sign-up-success";
    }
}
