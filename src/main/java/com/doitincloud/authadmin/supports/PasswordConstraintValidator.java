package com.doitincloud.authadmin.supports;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) { }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        List<String> messages = new ArrayList<>();
        if (ValidateUtils.isPasswordValid(password, messages)) {
            return true;
        }
        String messageTemplate = messages.stream().collect(Collectors.joining(" "));
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
