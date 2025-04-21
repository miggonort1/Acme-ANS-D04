
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ManagerValidator.class)
@Target(ElementType.TYPE)

public @interface ValidManager {

	//Standard validation properties -------------------------------------------------------------------------

	String message() default "Identifier number must start with the manager's initials.";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
