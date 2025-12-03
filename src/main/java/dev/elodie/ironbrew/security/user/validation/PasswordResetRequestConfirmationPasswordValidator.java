package dev.elodie.ironbrew.security.user.validation;

import dev.elodie.ironbrew.security.user.PasswordResetRequest;
import dev.elodie.ironbrew.security.user.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordResetRequestConfirmationPasswordValidator implements
        ConstraintValidator<ConfirmationPassword, PasswordResetRequest> {
    private String message;

    @Override
    public void initialize(ConfirmationPassword constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(PasswordResetRequest passwordResetRequest, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = passwordResetRequest.getPassword().equals(passwordResetRequest.getConfirmationPassword());

        if(!isValid){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(message)
                    .addPropertyNode( "confirmationPassword" ).addConstraintViolation();
        }

        return isValid;
    }
}
