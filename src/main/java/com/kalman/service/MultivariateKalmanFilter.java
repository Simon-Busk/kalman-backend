package com.kalman.service;

import com.kalman.service.exceptions.InvalidMeasurementException;
import org.ejml.simple.SimpleMatrix;
import lombok.Getter;

/**
 * Simple N-D Kalman filter engine.
 * Stateful: each instance tracks its own estimate (x) and uncertainty (P).
 * Designed to be held inside a session-scoped Spring bean so that one filter
 * instance corresponds to exactly one client session.
 * Algorithm (per update call)
 * ───────────────────────────
 *  Predict:
 *    x⁻ = F*x           (state does not change on its own – constant model) ->
 *    P⁻ = F*P*F^T + Q       (uncertainty grows by process noise)
 *  Update:
 *    K  = P*H^T⁻*(H*P*H^T + R)^-1            (Kalman gain)
 *    x  = x⁻ + K · (z − H*x⁻)       (fuse measurement z into estimate)
 *    P  = (I − K*H) · P⁻ * (I-K*H)^T + K*R*K^T             (reduce uncertainty)
 */
public class MultivariateKalmanFilter {
    private final SimpleMatrix Q; // Process uncertainty n_x*n_x
    private final SimpleMatrix R; // Measurement uncertainty n_z*n_z
    private final SimpleMatrix F; //State transition matrix. n_x*n_x
    private final SimpleMatrix H; //Observation model (difference between measurement and wanted state) n_z*n_x

    private SimpleMatrix x; // State estimate n_x*1
    private SimpleMatrix P; // Uncertainty for estimate  n_x*n_x
    private SimpleMatrix z; //Latest measurement (n_z*1).

    @Getter
    private int step = 0;

    public MultivariateKalmanFilter(SimpleMatrix initialEstimate,
                                    SimpleMatrix initialUncertainty,
                                    SimpleMatrix processNoise,
                                    SimpleMatrix measurementNoise,
                                    SimpleMatrix stateTransition,
                                    SimpleMatrix observation) {
        this.x = initialEstimate;
        this.P = initialUncertainty;
        this.Q = processNoise;
        this.R = measurementNoise;
        this.F = stateTransition;
        this.H = observation;
    }

    public void update(SimpleMatrix measurement) {
        if (measurement.getNumRows() != H.getNumRows()) {
            String errorMessage = "Number of data points in the measurement doesn't fit the filter.";
            errorMessage += "\nDatapoints in filters observation model: " + H.getNumRows();
            errorMessage += "\nDatapoints in measurement: " + measurement.getNumRows();
            throw new InvalidMeasurementException(errorMessage);
        }
        z = measurement;

        // Prediction
        SimpleMatrix pPred = (F.mult(P).mult(F.transpose())).plus(Q);
        SimpleMatrix xPred = F.mult(x);

        // Update
        //Kalman gain
        SimpleMatrix S = H.mult(pPred).mult(H.transpose()).plus(R);
        SimpleMatrix k = pPred.mult(H.transpose()).mult(S.invert());
        // New state estimate
        x = xPred.plus(k.mult(measurement.minus(H.mult(xPred))));
        // New covariance matrix
        SimpleMatrix IK = SimpleMatrix.identity(P.getNumRows()).minus(k.mult(H));
        P = IK.mult(pPred).mult(IK.transpose()).plus(k.mult(R).mult(k.transpose()));
        // Step update
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

    // Returns the next kalman gain (independent of the measurement).
    public double[][] getKalmanGain() {
            SimpleMatrix pPred = (F.mult(P).mult(F.transpose())).plus(Q);
            SimpleMatrix S = H.mult(pPred).mult(H.transpose()).plus(R);
            SimpleMatrix KGain = pPred.mult(H.transpose()).mult(S.invert());
        return KGain.toArray2();
    }
    public double[] getLatestMeasurement() {
        double[] measurement = new double[z.getNumElements()];
        for (int i = 0; i < measurement.length; i++) {
            measurement[i] = z.get(i);
        }
        return measurement;
    }
}
