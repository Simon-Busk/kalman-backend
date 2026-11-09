package com.kalman.model.validators;

import com.kalman.model.MultivariateInitRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ejml.simple.SimpleMatrix;

public class MatrixSizeValidator implements ConstraintValidator<MatrixSize, MultivariateInitRequest> {

    @Override
    public boolean isValid(MultivariateInitRequest request, ConstraintValidatorContext context) {
        SimpleMatrix firstMeasurement;
        SimpleMatrix measurementNoise;
        SimpleMatrix processNoise;
        try {
            firstMeasurement = request.getFirstMeasurement();
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage() + " in first measurement.", context);
        }
        try {
            measurementNoise = request.getMeasurementNoise();
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage() + " in measurement noise matrix.", context);
        }
        try {
            processNoise = request.getProcessNoise();
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage() + " in process noise matrix.", context);
        }
        int n = firstMeasurement.getNumRows();

        if (measurementNoise.getNumRows() != measurementNoise.getNumCols() || processNoise.getNumRows() != processNoise.getNumCols()) {
            return fail("Matrices for process noise and measurement noise have to be square matrices.", context);
        }
        if (n != measurementNoise.getNumCols()) {
            return fail(
                    "Measurement noise matrix have to have the same number of rows and columns as the first measurement has data points.",
                    context);
        }
        if (n != processNoise.getNumCols()) {
            return fail(
                    "Process noise matrix have to have the same number of rows and columns as the first measurement has data points.",
                    context);
        }
        return true;
    }

    private boolean fail(String message, ConstraintValidatorContext context){
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }

}