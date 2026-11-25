package com.kalman.service;

import com.kalman.model.MultivariateFilterState;
import com.kalman.model.MultivariateInitRequest;
import com.kalman.model.MultivariateMeasurementRequest;
import com.kalman.model.MultivariateStepResult;
import org.ejml.simple.SimpleMatrix;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@SessionScope
public class MultivariateKalmanService {

    private final Map<String, MultivariateKalmanFilter> filters = new ConcurrentHashMap<>();

    public MultivariateFilterState initialise(MultivariateInitRequest initRequest, String id) {
        MultivariateKalmanFilter filter = new MultivariateKalmanFilter(
                initRequest.getInitialState(),
                initRequest.getInitialUncertainty(),
                initRequest.getProcessNoise(),
                initRequest.getMeasurementNoise(),
                initRequest.getStateTransition(),
                initRequest.getObservationModel());
        filters.put(id, filter);

        return currentState(id);
    }

    public MultivariateStepResult step(MultivariateMeasurementRequest request, String id) {
        requireInitialised(id);
        SimpleMatrix measurement = request.getMeasurement();
        MultivariateKalmanFilter filter = filters.get(id);
        filter.update(measurement);

        return new MultivariateStepResult(filter.getStep(),
                filter.getLatestMeasurement(),
                filter.getEstimate(),
                filter.getUncertainty(),
                filter.getKalmanGain());
    }

    public MultivariateFilterState currentState(String id) {
        requireInitialised(id);
        MultivariateKalmanFilter filter = filters.get(id);
        return new MultivariateFilterState(filter.getStep(), filter.getEstimate(), filter.getUncertainty(), filter.getKalmanGain());
    }

    public void reset(String id) {
        filters.remove(id);
    }

    public boolean isNotInitialised(String id) {
        return !filters.containsKey(id);
    }

    //Helper methods
    private void requireInitialised(String id) {
        if (!filters.containsKey(id)) {
            throw new IllegalStateException(
                    "Filter not initialised. POST /api/kalman/{id}/multivariate/init first.");
        }
    }
}
