package app.project.ably.common.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegNoValidator.class)
public @interface RegNoValid {
    String message() default "{exception.validate.regNo}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
