package dev.elodie.ironbrew.security.user.validation;

import dev.elodie.ironbrew.security.user.ChangePasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChangePasswordRequestConfirmationPasswordValidator implements ConstraintValidator<ConfirmationPassword, ChangePasswordRequest> {
    private String message;

    @Override
    public void initialize(ConfirmationPassword constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(ChangePasswordRequest changePasswordRequest, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword());

        if(!isValid){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(message)
                    .addPropertyNode( "confirmPassword" ).addConstraintViolation();
        }

        return isValid;
    }
}
