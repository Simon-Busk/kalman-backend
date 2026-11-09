package com.kalman.model;

import com.kalman.model.validators.ExtendedCheck;
import com.kalman.model.validators.Finite;
import com.kalman.model.validators.PositiveDefinite;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.ejml.simple.SimpleMatrix;

@Data
@GroupSequence({MultivariateInitRequest.class ,ExtendedCheck.class})
public class MultivariateInitRequest {
    @NotEmpty
    @Finite(groups = ExtendedCheck.class)
    private double[] firstMeasurement;
    @NotEmpty
    @Finite(groups = ExtendedCheck.class)
    private double[][] processNoise;
    @NotEmpty
    @Finite(groups = ExtendedCheck.class)
    @PositiveDefinite(groups = ExtendedCheck.class)
    private double[][] measurementNoise;

    public SimpleMatrix getFirstMeasurement() {
        return new SimpleMatrix(firstMeasurement);
    }

    public SimpleMatrix getProcessNoise() {
        return new SimpleMatrix(processNoise);
    }

    public SimpleMatrix getMeasurementNoise() {
        return new SimpleMatrix(measurementNoise);
    }
}
