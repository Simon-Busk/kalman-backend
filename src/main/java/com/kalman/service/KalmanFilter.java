package com.kalman.service;

import lombok.Getter;

/**
 * Core 1-D Kalman filter engine.
 * Stateful: each instance tracks its own estimate (x) and uncertainty (P).
 * Designed to be held inside a session-scoped Spring bean so that one filter
 * instance corresponds to exactly one client session.
 * Algorithm (per update call)
 * ───────────────────────────
 *  Predict:
 *    x⁻ = x           (state does not change on its own – constant model)
 *    P⁻ = P + Q       (uncertainty grows by process noise)
 *  Update:
 *    K  = P⁻ / (P⁻ + R)            (Kalman gain)
 *    x  = x⁻ + K · (z − x⁻)       (fuse measurement z into estimate)
 *    P  = (1 − K) · P⁻             (reduce uncertainty)
 */
public class KalmanFilter {

    private final double Q;   // process noise covariance
    private final double R;   // measurement noise covariance

    private double x;         // current state estimate
    private double P;         // current error covariance
    private double K;
    @Getter
    private int    step = 0;  // number of measurements processed

    public KalmanFilter(double initialEstimate,
                        double initialUncertainty,
                        double processNoise,
                        double measurementNoise) {
        this.x = initialEstimate;
        this.P = initialUncertainty;
        this.Q = processNoise;
        this.R = measurementNoise;
        this.K = (P+Q)/(P+Q+R);
    }

    /** Run one predict→update cycle. Returns the new state estimate. */
    public double update(double measurement) {
        // Predict
        double pPred = P + Q;

        x = x + K * (measurement - x);
        P = (1.0 - K) * pPred;
        step++;
        K = (P+Q)/(P+Q+R); //Update the kalman gain for the next step

        return x;
    }

    public double getEstimate()    { return x; }
    public double getUncertainty() { return P; }
    public double getKalmanGain()  { return K; } // gain for next step
}
