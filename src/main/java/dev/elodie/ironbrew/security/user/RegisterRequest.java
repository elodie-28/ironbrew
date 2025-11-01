package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.security.user.validation.ConfirmationPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

//TODO proper validation
//TODO email verification
@ConfirmationPassword
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isEmailPublic() {
        return isEmailPublic;
    }

    public void setEmailPublic(boolean emailPublic) {
        isEmailPublic = emailPublic;
    }

    public String getConfirmationPassword() {
        return confirmationPassword;
    }

    public void setConfirmationPassword(String confirmationPassword) {
        this.confirmationPassword = confirmationPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
