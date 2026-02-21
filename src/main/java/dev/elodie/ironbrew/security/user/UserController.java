package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.exception.EmailAlreadyTakenException;
import dev.elodie.ironbrew.exception.InvalidTokenException;
import dev.elodie.ironbrew.exception.NameAlreadyTakenException;
import dev.elodie.ironbrew.exception.UserNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

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

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token) {
        try{
            userService.confirmToken(token);
        } catch (InvalidTokenException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return "registration-confirmation";
    }

    @GetMapping("/forgot-password")
    public String passwordReset(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute ForgotPasswordRequest forgotPasswordRequest) throws MessagingException {
        try{
            userService.createPasswordResetToken(forgotPasswordRequest);
        } catch (UserNotFoundException e){
            return "redirect:/forgot-password?sent";
        }

        return "redirect:/forgot-password?sent";
    }

    @GetMapping("/password-reset")
    public String passwordReset(Model model, @RequestParam("token") String token) {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setToken(token);
        model.addAttribute("passwordResetRequest", passwordResetRequest);
        return "password-reset";
    }

    @PostMapping("/password-reset")
    public String passwordResetPost(@Valid @ModelAttribute PasswordResetRequest passwordResetRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "password-reset";
        }

        try{
            userService.changePassword(passwordResetRequest);
        }catch (UserNotFoundException | InvalidTokenException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/?password_reset_success";
    }

    @GetMapping("/my-account")
    public String myAccount(Model model, Principal principal) {
        String email = principal.getName();
        AccountDto user = userService.findAccountDtoByEmail(email);
        model.addAttribute("user", user);
        return "my-account";
    }

    @PostMapping("/my-account")
    public String updateMyAccount(@Valid @ModelAttribute("user") AccountDto accountDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "my-account";
        }

        String email = principal.getName();
        try{
            userService.updateFromAccountDto(accountDto, email);
        } catch (NameAlreadyTakenException e){
            bindingResult.rejectValue("name", "nameAlreadyTaken", "Name is already taken");
            return "my-account";
        }


        return "redirect:/my-account?success";
    }
}
