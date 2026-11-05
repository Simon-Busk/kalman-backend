package com.kalman.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MultivariateFilterState {
    private int step;
    private double[] estimate;
    private double[][] uncertainty;
    private double[][] kalmanGain;
}
