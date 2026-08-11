package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.security.user.validation.ConfirmationPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@ConfirmationPassword
@Setter
@Getter
public class ChangePasswordRequest {
    @NotBlank(message = "Old password is required")
    private String oldPassword;
    @NotBlank(message = "New password is required")
    @Pattern(
            regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
            message = "Password must contain at least 8 characters including 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character (#?!@$%^&*-)"
    )
    private String newPassword;
    @NotBlank(message = "Confirmation password is required")
    private String confirmPassword;
}
