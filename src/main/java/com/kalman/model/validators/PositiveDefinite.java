package com.kalman.model.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositiveDefiniteValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveDefinite {
    String message() default "Matrix has to be positive definite";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    boolean semiPositive() default false;
}
