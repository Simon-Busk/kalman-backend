package com.kalman.model.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FiniteVectorValidator implements ConstraintValidator<Finite, double[]> {
    @Override
    public boolean isValid(double [] vector, ConstraintValidatorContext context) {
        for (double value: vector) {
            if (!Double.isFinite(value)) {
                return false;
            }
        }
        return true;
    }
}
