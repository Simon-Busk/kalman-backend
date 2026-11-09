package com.kalman.model.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MatrixSizeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MatrixSize {
    String message() default "Matrices sizes have to match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
