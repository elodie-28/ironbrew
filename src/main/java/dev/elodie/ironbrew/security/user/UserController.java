package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.exception.EmailAlreadyTakenException;
import dev.elodie.ironbrew.exception.InvalidTokenException;
import dev.elodie.ironbrew.exception.NameAlreadyTakenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class UserController {
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public UserController(
            UserService userService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
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
            User user = userService.register(registerRequest);

            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        }catch (EmailAlreadyTakenException e){
            bindingResult.rejectValue("email", "emailAlreadyTaken", "Email is already taken");
            return "sign-up";
        }catch (NameAlreadyTakenException e){
            bindingResult.rejectValue("name", "nameAlreadyTaken", "Name is already taken");
            return "sign-up";
        }

        return "redirect:/?sign_up_success";
    }

    @GetMapping("/registrationConfirm")
    public String registrationConfirm(@RequestParam("token") String token) {
        try{
            userService.confirmToken(token);
        } catch (InvalidTokenException e){
            return "registration-confirmation-invalid";
        }


        return "registration-confirmation";
    }
}
