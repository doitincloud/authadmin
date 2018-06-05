package doitincloud.authadmin.supports;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = TermSetValidator.class)
@Target({ TYPE, FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidTermSet {

    String message() default "Entry not found for the type";
    String value() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
