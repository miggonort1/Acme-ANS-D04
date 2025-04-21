
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = CustomerIdentifierValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCustomerIdentifier {

	String message() default "Identifier must start with the customer's initials.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
