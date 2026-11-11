package com.kalman.controller;

import com.kalman.model.*;
import com.kalman.service.KalmanService;
import com.kalman.service.KalmanSimulation;
import com.kalman.service.MultivariateKalmanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * REST API for the step-by-step Kalman filter.
 * Endpoints
 * ─────────
 *  POST   /api/kalman/init    – create / reset filter with config
 *  POST   /api/kalman/step    – feed one measurement, get estimate back
 *  GET    /api/kalman/state   – peek at current filter state
 *  DELETE /api/kalman/reset   – wipe the filter for this session
 * Session scope means each browser tab gets its own independent filter.
 * The JSESSIONID cookie is set automatically by Spring.
 */
@RestController
@RequestMapping("/api/kalman")
@RequiredArgsConstructor
public class KalmanController {

    private final KalmanService kalmanService;
    private final MultivariateKalmanService multivariateKalmanService;

    // ── POST /api/kalman/init ─────────────────────────────────────────────────

    /**
     * Initialise (or re-initialise) the filter for this session.
     * Example – seed with first measurement (recommended):
     * <pre>
     * POST /api/kalman/init
     * {
     *   "seedWithFirstMeasurement": true,
     *   "firstMeasurement": 24.7,
     *   "processNoise": 0.01,
     *   "measurementNoise": 4.0
     * }
     * </pre>
     *
     * Example – arbitrary initial guess:
     * <pre>
     * POST /api/kalman/init
     * {
     *   "seedWithFirstMeasurement": false,
     *   "initialEstimate": 0.0,
     *   "initialUncertainty": 10.0,
     *   "processNoise": 0.01,
     *   "measurementNoise": 4.0
     * }
     * </pre>
     */
    @PostMapping("/init")
    public ResponseEntity<FilterState> init(@Valid @RequestBody InitRequest request) {
        FilterState state = kalmanService.initialise(request);
        return ResponseEntity.ok(state);
    }

    // ── POST /api/kalman/step ─────────────────────────────────────────────────

    /**
     * Feed one raw measurement into the running filter.
     *
     * <pre>
     * POST /api/kalman/step
     * { "measurement": 25.3 }
     *
     * Response:
     * {
     *   "step": 2,
     *   "measurement": 25.3,
     *   "estimate": 25.1,
     *   "uncertainty": 1.92,
     *   "kalmanGain": 0.32
     * }
     * </pre>
     *
     * Returns 409 Conflict if the filter has not been initialised yet.
     */
    @PostMapping("/step")
    public ResponseEntity<?> step(@Valid @RequestBody MeasurementRequest request) {
        if (!kalmanService.isInitialised()) {
            return ResponseEntity.status(409)
                    .body(Map.of("message","Filter not initialised. POST /api/kalman/init first."));
        }
        StepResult result = kalmanService.step(request);
        return ResponseEntity.ok(result);
    }

    // ── GET /api/kalman/state ─────────────────────────────────────────────────

    /**
     * Return the current filter state without advancing it.
     * Useful for polling or reconnecting after a page refresh.
     */
    @GetMapping("/state")
    public ResponseEntity<?> state() {
        if (!kalmanService.isInitialised()) {
            return ResponseEntity.status(409)
                    .body(Map.of("message","Filter not initialised. POST /api/kalman/init first."));
        }
        return ResponseEntity.ok(kalmanService.currentState());
    }

    // ── DELETE /api/kalman/reset ──────────────────────────────────────────────

    /** Wipe the filter for this session so a new init can begin. */
    @DeleteMapping("/reset")
    public ResponseEntity<?> reset() {
        if (!kalmanService.isInitialised()) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Filter was not initialised and therefore nothing has been done."));
        }
        kalmanService.reset();
        return ResponseEntity.ok(Map.of("message", "Kalman filter was succesfully reset and a new filter can be initialised."));
    }

    @PostMapping("/simulation")
    public ResponseEntity<?> simulation(@Valid @RequestBody SimulationRequest request) {
        KalmanSimulation simulation = new KalmanSimulation();
        simulation.setMean(request.getMean());
        simulation.setVariance(request.getVariance());
        ArrayList<StepResult> result = simulation.Simulate(request.getNumberOfMeasurements());
        return ResponseEntity.ok(result);
    }

    // Multivariate api

    @PostMapping("/multivariate/init")
    public ResponseEntity<MultivariateFilterState> multivariateInit(@Valid @RequestBody MultivariateInitRequest init) {
        MultivariateFilterState filterState = multivariateKalmanService.initialise(init);
        return ResponseEntity.ok((filterState));
    }

    @PostMapping("multivariate/step")
    public ResponseEntity<?> multivariateStep(@Valid @RequestBody MultivariateMeasurementRequest stepRequest) {
        if (!multivariateKalmanService.isInitialised()) {
            return ResponseEntity.status(409)
                    .body(Map.of("message","Multivariate filter not initialised. POST /api/kalman/multivariate/init first."));
        }
        MultivariateStepResult stepResult = multivariateKalmanService.step(stepRequest);
        return ResponseEntity.ok(stepResult);
    }

    @GetMapping("/multivariate/state")
    public ResponseEntity<?> multivariateState() {
        if (!multivariateKalmanService.isInitialised()) {
            return ResponseEntity.status(409)
                    .body(Map.of("message","Multivariate filter not initialised. POST /api/kalman/multivariate/init first."));
        }
        return ResponseEntity.ok(multivariateKalmanService.currentState());
    }

    @DeleteMapping("/multivariate/reset")
    public ResponseEntity<?> multivariateReset() {
        multivariateKalmanService.reset();
        return ResponseEntity.ok(Map.of("message","Multivariate kalman filter was reset and a new filter can be initialised."));
    }
}
