package dev.elodie.ironbrew.security.user.validation;

import dev.elodie.ironbrew.security.user.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegisterRequestConfirmationPasswordValidator implements
        ConstraintValidator<ConfirmationPassword, RegisterRequest> {
    private String message;

    @Override
    public void initialize(ConfirmationPassword constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(RegisterRequest registerRequest, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = registerRequest.getPassword().equals(registerRequest.getConfirmationPassword());

        if(!isValid){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(message)
                    .addPropertyNode( "confirmationPassword" ).addConstraintViolation();
        }

        return isValid;
    }
}
