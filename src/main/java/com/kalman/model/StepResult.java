package com.kalman.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Full result returned after POST /api/kalman/step */
@Data
@AllArgsConstructor
public class StepResult {
    /** Which measurement number this is (1-based). */
    private int    step;

    /** The raw measurement that was fed in. */
    private double measurement;

    /** The filter's refined state estimate after this measurement. */
    private double estimate;

    /** Current error covariance P – how uncertain the filter is. */
    private double uncertainty;

    /**
     * Kalman gain K that will be applied on the *next* step.
     * Useful for the frontend to show how much the filter will trust
     * the next measurement.
     */
    private double kalmanGain;
}
