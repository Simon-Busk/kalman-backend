package com.kalman.service;

import com.kalman.model.FilterState;
import com.kalman.model.InitRequest;
import com.kalman.model.MeasurementRequest;
import com.kalman.model.StepResult;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Session-scoped service: one instance per HTTP session (one per browser tab).
 *
 * Spring creates this bean when the first request arrives in a new session and
 * destroys it when the session expires (default: 30 min of inactivity).
 * This means each client gets its own isolated Kalman filter state.
 */
@Component
@SessionScope
public class KalmanService {

    private KalmanFilter filter;
    private boolean initialised = false;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Initialise (or re-initialise) the filter for this session.
     * Uses the first measurement as the initial estimate if the client
     * sends {@code seedWithFirstMeasurement = true}.
     */
    public FilterState initialise(InitRequest req) {
        double x0 = req.isSeedWithFirstMeasurement()
                ? req.getFirstMeasurement()          // principled: x₀ = z₁
                : req.getInitialEstimate();           // caller-supplied guess

        double p0 = req.isSeedWithFirstMeasurement()
                ? req.getMeasurementNoise()           // P₀ = R when seeding from measurement
                : req.getInitialUncertainty();

        filter      = new KalmanFilter(x0, p0, req.getProcessNoise(), req.getMeasurementNoise());
        initialised = true;

        return currentState();
    }

    /**
     * Feed one measurement into the running filter and return the updated state.
     */
    public StepResult step(MeasurementRequest req) {
        requireInitialised();
        double measurement = req.getMeasurement();
        double estimate    = filter.update(measurement);

        return new StepResult(
                filter.getStep(),
                measurement,
                estimate,
                filter.getUncertainty(),
                filter.getKalmanGain()
        );
    }

    /** Return the current filter state without advancing it. */
    public FilterState currentState() {
        requireInitialised();
        return new FilterState(
                filter.getStep(),
                filter.getEstimate(),
                filter.getUncertainty(),
                filter.getKalmanGain()
        );
    }

    /** Reset: wipe the filter so the session can start fresh. */
    public void reset() {
        filter      = null;
        initialised = false;
    }

    public boolean isInitialised() { return initialised; }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void requireInitialised() {
        if (!initialised) {
            throw new IllegalStateException(
                "Filter not initialised. POST /api/kalman/init first.");
        }
    }
}
