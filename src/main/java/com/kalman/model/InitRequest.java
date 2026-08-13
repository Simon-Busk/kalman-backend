package com.kalman.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Request body for POST /api/kalman/init
 *
 * Two usage patterns:
 *
 *  1) Seed with first measurement (recommended):
 *     { "seedWithFirstMeasurement": true,
 *       "firstMeasurement": 24.7,
 *       "processNoise": 0.01,
 *       "measurementNoise": 4.0 }
 *
 *  2) Arbitrary initial estimate:
 *     { "seedWithFirstMeasurement": false,
 *       "initialEstimate": 0.0,
 *       "initialUncertainty": 10.0,
 *       "processNoise": 0.01,
 *       "measurementNoise": 4.0 }
 */
@Data
public class InitRequest {

    /** If true, x₀ = firstMeasurement and P₀ = measurementNoise. */
    private boolean seedWithFirstMeasurement = true;

    /** Used as x₀ when seedWithFirstMeasurement = true. */
    private double firstMeasurement = 0.0;

    /** Used as x₀ when seedWithFirstMeasurement = false. */
    private double initialEstimate = 0.0;

    /** Used as P₀ when seedWithFirstMeasurement = false. */
    @Positive
    private double initialUncertainty = 10.0;

    /** Q – how much the true state can drift per step. */
    @NotNull
    @Positive
    private double processNoise = 0.01;

    /** R – variance of the sensor (σ²). */
    @NotNull
    @Positive
    private double measurementNoise = 4.0;
}
