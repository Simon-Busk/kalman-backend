package com.kalman.model;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;

@Data
public class MultivariateInitRequest {
    private double[] firstMeasurement;
    private double[][] processNoise;
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
