package com.doitincloud.authadmin.supports;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private String defaultRegion = "";

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) { }

    @Override
    public boolean isValid(String number, ConstraintValidatorContext context) {
        List<String> messages = new ArrayList<>();
        if (ValidateUtils.isPhoneNumberValid(number, defaultRegion, messages)) {
            return true;
        }
        String messageTemplate = messages.stream().collect(Collectors.joining(" "));
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
