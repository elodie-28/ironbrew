package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.security.user.validation.ConfirmationPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@ConfirmationPassword
public class PasswordResetRequest {
    private String token;
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
            message = "Password must contain at least 8 characters including 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character (#?!@$%^&*-)"
    )
    private String password;
    @NotBlank(message = "Confirmation password is required")
    private String confirmationPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmationPassword() {
        return confirmationPassword;
    }

    public void setConfirmationPassword(String confirmationPassword) {
        this.confirmationPassword = confirmationPassword;
    }
}
