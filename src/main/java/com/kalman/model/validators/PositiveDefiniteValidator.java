package com.kalman.model.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

public class PositiveDefiniteValidator implements ConstraintValidator<PositiveDefinite, double[][]> {
    @Override
    public boolean isValid(double[][] matrix, ConstraintValidatorContext context) {
        //Check for square matrix
        if (matrix.length != matrix[0].length) {
            return fail(context, "Matrix must be a square matrix.");
        }

        SimpleMatrix matrixRep = new SimpleMatrix(matrix);
        //Check symmetry
        if (!matrixRep.isIdentical(matrixRep.transpose(), 0)) {
            return fail(context, "Matrix must be symmetrical");
        }

        //Check eigenvalues to guarantee positive definite
        SimpleEVD<SimpleMatrix> eigenValues =  matrixRep.eig();
        for (int i = 0; i < matrix.length; i++) {
            double eigenValue = eigenValues.getEigenvalue(i).getReal();
            if (eigenValue <= 0) {
                return fail(context, "Matrix must be positive definite but found eigenvalue less than or equal to 0, eigenvalue: " + eigenValue);
            }
        }

        return true;
    }

    private boolean fail(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
