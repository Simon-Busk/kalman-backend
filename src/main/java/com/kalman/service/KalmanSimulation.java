package com.kalman.service;

import com.kalman.model.StepResult;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;
import lombok.Setter;

@Component
public class KalmanSimulation {
    @Getter
    private KalmanFilter filter;
    @Setter
    private double variance;
    @Setter
    private double mean;
    private final Random rng = new Random();

    public ArrayList<StepResult> Simulate(int number) {
        ArrayList<StepResult> result = new ArrayList<>();
        double measurement = Measurement();
        filter = new KalmanFilter(measurement, variance, 0.01, variance);
        result.add(new StepResult(filter.getStep(), measurement, filter.getEstimate(), filter.getUncertainty(), filter.getKalmanGain()));
        for (int i = 1; i <= number; i++) {
            measurement = Measurement();
            filter.update(measurement);
            result.add(new StepResult(filter.getStep(), measurement, filter.getEstimate(), filter.getUncertainty(), filter.getKalmanGain()));
        }
        return result;
    }

    //Helper method to generate random measurements from variance and mean using Gaussian distribution.
    private double Measurement() {
        return mean + Math.sqrt(variance) * rng.nextGaussian();
    }
}
