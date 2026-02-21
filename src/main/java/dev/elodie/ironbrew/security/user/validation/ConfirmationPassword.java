package dev.elodie.ironbrew.security.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {
        RegisterRequestConfirmationPasswordValidator.class,
        PasswordResetRequestConfirmationPasswordValidator.class,
        ChangePasswordRequestConfirmationPasswordValidator.class
})
@Target( {ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfirmationPassword {
    String message() default "Confirmation password doesn't match the password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
