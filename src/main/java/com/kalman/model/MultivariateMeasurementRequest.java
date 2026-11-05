package com.kalman.model;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;

@Data
public class MultivariateMeasurementRequest {

    private double[] measurement;

    public SimpleMatrix getMeasurement() {
        return new SimpleMatrix(measurement);
    }

}
