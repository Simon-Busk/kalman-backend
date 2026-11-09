package com.kalman.model.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FiniteMatrixValidator implements ConstraintValidator<Finite, double[][]> {
    @Override
    public boolean isValid(double[][] matrix, ConstraintValidatorContext context) {
        for (double[] row: matrix) {
            if (row == null) {
                return false;
            }
            for (double value: row) {
                if (!Double.isFinite(value)) {
                    return false;
                }
            }
        }

        return true;
    }
}
