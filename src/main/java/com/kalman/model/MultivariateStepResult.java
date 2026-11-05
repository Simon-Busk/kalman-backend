package com.kalman.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MultivariateStepResult {

    private int step;
    private double[] measurement;
    private double[] estimate;
    private double[][] uncertainty;
    private double[][] kalmanGain;
}
