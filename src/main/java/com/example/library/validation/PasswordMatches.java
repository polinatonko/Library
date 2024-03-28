package com.example.library.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
    String message() default "Passwords don't match!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
