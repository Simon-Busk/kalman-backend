package com.kalman.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Current snapshot of the filter – returned by init and GET /state. */
@Data
@AllArgsConstructor
public class FilterState {
    private int    step;
    private double estimate;
    private double uncertainty;
    private double kalmanGain;
}
