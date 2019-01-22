package at.alladin.nettest.shared.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @author alladin-IT GmbH (fk@alladin.at)
 *
 */	
@Retention(RetentionPolicy.RUNTIME)
@NotNull
@Size(min=1)
@Constraint(validatedBy = { })
public @interface NotEmpty {
    String message() default "{javax.validation.constraints.NotEmpty.message}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
