
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmployeeCodeValidator.class)

public @interface ValidEmployeeCode {

	String message() default "El código de empleado debe comenzar con las iniciales de la identidad del usuario";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
