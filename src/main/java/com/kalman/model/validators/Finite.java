package com.kalman.model.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {FiniteVectorValidator.class, FiniteMatrixValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Finite {
    String message() default "All inputs in the matrix/vector must not be NaN and less than infinite";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
