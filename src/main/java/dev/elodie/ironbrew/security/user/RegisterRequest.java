package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.security.user.validation.ConfirmationPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@ConfirmationPassword
@Setter
@Getter
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email")
    private String email;
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
            message = "Password must contain at least 8 characters including 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character (#?!@$%^&*-)"
    )
    private String password;
    @NotBlank(message = "Confirmation password is required")
    private String confirmationPassword;
    private boolean isEmailPublic;
    @URL(message = "Please enter a valid URL")
    private String website;
    @NotBlank(message = "Name is required")
    private String name;
}
