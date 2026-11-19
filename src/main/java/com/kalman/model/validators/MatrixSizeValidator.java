package com.kalman.model.validators;

import com.kalman.model.MultivariateInitRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ejml.simple.SimpleMatrix;

public class MatrixSizeValidator implements ConstraintValidator<MatrixSize, MultivariateInitRequest> {

    @Override
    public boolean isValid(MultivariateInitRequest request, ConstraintValidatorContext context) {
        SimpleMatrix initialState;
        SimpleMatrix measurementNoise;
        SimpleMatrix processNoise;
        SimpleMatrix stateTransition;
        SimpleMatrix observationModel;
        SimpleMatrix initialUncertainty;

        // Try to convert arrays to SimpleMatrix.
        // If array is in wrong format catch IllegalArgumentException and throw fail message.
        try {
            initialState = request.getInitialState();
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
        try {
            observationModel = request.getObservationModel();
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage() + " in the observation model matrix.", context);
        }
        try {
            initialUncertainty = request.getInitialUncertainty();
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage() + " in the observation model matrix.", context);
        }

        // Check the matrix sizes
        int n_z = observationModel.getNumRows();
        int n_x = initialState.getNumRows();

        if (measurementNoise.getNumRows() != measurementNoise.getNumCols() || processNoise.getNumRows() != processNoise.getNumCols()) {
            return fail("Matrices for process noise and measurement noise have to be square matrices.", context);
        }
        if (stateTransition.getNumRows() != stateTransition.getNumCols()) {
            return fail("The state transition matrix have to be a square matrix.", context);
        }
        if (initialUncertainty.getNumCols() != initialUncertainty.getNumRows()) {
            return fail("The initial uncertainty matrix have to be a square matrix.", context);
        }
        if (n_z != measurementNoise.getNumCols()) {
            return fail(
                    "Measurement noise matrix has to have the same number of rows and columns as the measurement has data points.",
                    context);
        }
        if (n_x != processNoise.getNumCols()) {
            return fail(
                    "Process noise matrix has to have the same number of rows and columns as the state has data points.",
                    context);
        }
        if (n_x != stateTransition.getNumCols()) {
            return fail(
                    "State transition matrix has to have the same number of rows and columns as the state has data points.",
                    context);
        }
        if (n_x != observationModel.getNumCols()) {
            return fail(
                    "Observation model has to have same number of columns as the state has data points.",
                    context);
        }
        if (n_x != initialUncertainty.getNumRows()) {
            return  fail(
                    "Initial uncertainty has to have the same number of rows and columns as the state.",
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