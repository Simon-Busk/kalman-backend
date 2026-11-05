package com.kalman.service;

import com.kalman.model.MultivariateFilterState;
import com.kalman.model.MultivariateInitRequest;
import com.kalman.model.MultivariateMeasurementRequest;
import com.kalman.model.MultivariateStepResult;
import lombok.Getter;
import org.ejml.simple.SimpleMatrix;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class MultivariateKalmanService {
    private MultivariateKalmanFilter filter;
    @Getter
    private boolean initialised = false;

    public MultivariateFilterState initialise(MultivariateInitRequest initRequest) {
        filter = new MultivariateKalmanFilter(initRequest.getFirstMeasurement(), initRequest.getMeasurementNoise(), initRequest.getProcessNoise(), initRequest.getMeasurementNoise());
        initialised = true;

        return currentState();
    }

    public MultivariateStepResult step(MultivariateMeasurementRequest request) {
        requireInitialised();
        SimpleMatrix measurement = request.getMeasurement();
        filter.update(measurement);

        return new MultivariateStepResult(filter.getStep(),
                filter.getLatestMeasurement(),
                filter.getEstimate(),
                filter.getUncertainty(),
                filter.getKalmanGain());
    }

    public MultivariateFilterState currentState() {
        requireInitialised();
        return new MultivariateFilterState(filter.getStep(), filter.getEstimate(), filter.getUncertainty(), filter.getKalmanGain());
    }

    public void reset() {
        filter      = null;
        initialised = false;
    }

    //Helper methods
    private void requireInitialised() {
        if (!initialised) {
            throw new IllegalStateException(
                    "Filter not initialised. POST /api/kalman/multivariate/init first.");
        }
    }
}
