package ru.grishuchkov.cloudfilestorage.util.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.grishuchkov.cloudfilestorage.dto.UserRegistration;

public class PasswordMatchesValidation implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        UserRegistration user = (UserRegistration) value;
        return user.getPassword().equals(user.getPasswordConfirm());
    }
}
