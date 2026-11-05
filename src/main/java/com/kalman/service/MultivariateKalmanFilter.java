package com.kalman.service;

import org.ejml.simple.SimpleMatrix;
import lombok.Getter;

/**
 * Simple N-D Kalman filter engine.
 * Only position is measured and estimated (no velocity, acceleration or other dependent measurements.)
 *
 * Stateful: each instance tracks its own estimate (x) and uncertainty (P).
 * Designed to be held inside a session-scoped Spring bean so that one filter
 * instance corresponds to exactly one client session.
 *
 * Algorithm (per update call)
 * ───────────────────────────
 *  Predict:
 *    x⁻ = x           (state does not change on its own – constant model) ->
 *    P⁻ = P + Q       (uncertainty grows by process noise)
 *
 *  Update:
 *    K  = P⁻*(P⁻ + R)^-1            (Kalman gain)
 *    x  = x⁻ + K · (z − x⁻)       (fuse measurement z into estimate)
 *    P  = (1 − K) · P⁻             (reduce uncertainty)
 */
public class MultivariateKalmanFilter {
    private final SimpleMatrix Q;
    private final SimpleMatrix R;

    private SimpleMatrix x; //This is n*1 vector
    private SimpleMatrix P;
    private SimpleMatrix z; //Latest measurement.

    @Getter
    private int step = 0;

    public MultivariateKalmanFilter(SimpleMatrix initialEstimate,
                                    SimpleMatrix initialUncertainty,
                                    SimpleMatrix processNoise,
                                    SimpleMatrix measurementNoise) {
        this.x = initialEstimate;
        this.P = initialUncertainty;
        this.Q = processNoise;
        this.R = measurementNoise;
    }

    public void update(SimpleMatrix measurement) {
        SimpleMatrix pPred = P.plus(Q);
        z = measurement;
        SimpleMatrix S = pPred.mult(R);
        SimpleMatrix K = pPred.mult(S.invert());
        x = x.plus(K.mult(measurement.minus(x)));
        P = SimpleMatrix.identity(P.getNumRows()).minus(K).mult(pPred);
        step++;
    }

    public double[] getEstimate() {
        double[] estimate = new double[x.getNumElements()];
        for (int i = 0; i < estimate.length; i++) {
            estimate[i] = x.get(i);
        }
        return estimate;
    }
    public double[][] getUncertainty() { return P.toArray2(); }
    public double[][] getKalmanGain() {
        SimpleMatrix K = P.plus(Q).mult((P.plus(Q).plus(R)).invert());
        return K.toArray2();
    }
    public double[] getLatestMeasurement() {
        double[] measurement = new double[z.getNumElements()];
        for (int i = 0; i < measurement.length; i++) {
            measurement[i] = z.get(i);
        }
        return measurement;
    }
}
