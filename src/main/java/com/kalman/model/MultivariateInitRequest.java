package com.kalman.model;

import com.kalman.model.validators.ExtendedCheck;
import com.kalman.model.validators.Finite;
import com.kalman.model.validators.MatrixSize;
import com.kalman.model.validators.PositiveDefinite;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.ejml.simple.SimpleMatrix;

@Data
@GroupSequence({MultivariateInitRequest.class ,ExtendedCheck.class})
@MatrixSize(groups = ExtendedCheck.class)
public class MultivariateInitRequest {
    @NotEmpty
    @Finite(groups = ExtendedCheck.class)
    private double[] initialState;
    @NotEmpty
    @Finite(groups = ExtendedCheck.class)
    @PositiveDefinite(groups = ExtendedCheck.class, semiPositive = true)
    private double[][] processNoise;
    @NotEmpty
    @Finite(groups = ExtendedCheck.class)
    @PositiveDefinite(groups = ExtendedCheck.class)
    private double[][] measurementNoise;
    @Finite
    private double[][] stateTransition;
    @Finite
    private double[][] observationModel;
    @Finite
    private double [][] initialUncertainty;


    public SimpleMatrix getInitialState() {
        return new SimpleMatrix(initialState);
    }

    public SimpleMatrix getProcessNoise() {
        return new SimpleMatrix(processNoise);
    }

    public SimpleMatrix getMeasurementNoise() {
        return new SimpleMatrix(measurementNoise);
    }

    public SimpleMatrix getStateTransition() {
        if (stateTransition != null) {
            return new SimpleMatrix(stateTransition);
        } else {
            return SimpleMatrix.identity(initialState.length);
        }
    }

    public SimpleMatrix getObservationModel() {
        if (observationModel != null) {
            return new SimpleMatrix(observationModel);
        } else {
            return SimpleMatrix.identity(initialState.length);
        }
    }

    public SimpleMatrix getInitialUncertainty() {
        if (initialUncertainty != null) {
            return new SimpleMatrix(initialUncertainty);
        } else {
            return getMeasurementNoise();
        }
    }
}
