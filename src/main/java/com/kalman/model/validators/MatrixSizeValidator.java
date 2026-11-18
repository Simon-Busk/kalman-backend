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
        SimpleMatrix stateTransition;
        try {
            firstMeasurement = request.getFirstMeasurement();
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage() + " in the first measurement.", context);
        }
        try {
            measurementNoise = request.getMeasurementNoise();
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage() + " in the measurement noise matrix.", context);
        }
        try {
            processNoise = request.getProcessNoise();
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage() + " in the process noise matrix.", context);
        }
        try {
            stateTransition = request.getStateTransition();
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage() + " in the state transition matrix.", context);
        }
        int n = firstMeasurement.getNumRows();

        if (measurementNoise.getNumRows() != measurementNoise.getNumCols() || processNoise.getNumRows() != processNoise.getNumCols()) {
            return fail("Matrices for process noise and measurement noise have to be square matrices.", context);
        }
        if (stateTransition != null && stateTransition.getNumRows() != stateTransition.getNumCols()) {
            return fail("The state transition matrix have to be a square matrix.", context);
        }
        if (n != measurementNoise.getNumCols()) {
            return fail(
                    "Measurement noise matrix has to have the same number of rows and columns as the first measurement has data points.",
                    context);
        }
        if (n != processNoise.getNumCols()) {
            return fail(
                    "Process noise matrix has to have the same number of rows and columns as the first measurement has data points.",
                    context);
        }
        if (stateTransition != null && n != stateTransition.getNumCols()) {
            return fail(
                    "State transition matrix has to have the same number of rows and columns as the first measurement has data points.",
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