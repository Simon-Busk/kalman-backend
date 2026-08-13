package com.kalman.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Request body for POST /api/kalman/step */
@Data
public class MeasurementRequest {

    /** The raw (noisy) sensor reading z to feed into the filter. */
    @NotNull
    private double measurement;
}
