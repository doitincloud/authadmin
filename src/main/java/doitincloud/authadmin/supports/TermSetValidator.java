package doitincloud.authadmin.supports;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TermSetValidator implements ConstraintValidator<ValidTermSet, Set<String>> {

    private String type;

    @Override
    public void initialize(ValidTermSet constraintAnnotation) { type = constraintAnnotation.value(); }

    @Override
    public boolean isValid(Set<String> value, ConstraintValidatorContext context) {
        List<String> messages = new ArrayList<>();
        if (ValidateUtils.isTermSetValid(value, type, messages)) {
            return true;
        }
        String messageTemplate = messages.stream().collect(Collectors.joining(" "));
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
