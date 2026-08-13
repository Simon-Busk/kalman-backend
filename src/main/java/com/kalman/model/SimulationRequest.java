package com.kalman.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SimulationRequest {

    @NotNull
    @Positive
    private int numberOfMeasurements;

    @NotNull
    @Positive
    private double variance;

    @NotNull
    private double mean;

}
