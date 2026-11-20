package com.kalman.model.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FiniteVectorValidator implements ConstraintValidator<Finite, double[]> {
    @Override
    public boolean isValid(double [] vector, ConstraintValidatorContext context) {
        // Null/Empty check should be done separate.
        if (vector == null) {
            return true;
        }
        for (double value: vector) {
            if (!Double.isFinite(value)) {
                return false;
            }
        }
        return true;
    }
}
