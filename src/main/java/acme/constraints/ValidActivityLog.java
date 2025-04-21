
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ActivityLogValidator.class)
@Target(ElementType.TYPE)

public @interface ValidActivityLog {

	//Standard validation properties -------------------------------------------------------------------------

	String message() default "{acme.validation.activityLog.beforeLeg}";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
