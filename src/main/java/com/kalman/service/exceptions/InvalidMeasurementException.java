package com.kalman.service.exceptions;

public class InvalidMeasurementException extends RuntimeException{
    public InvalidMeasurementException(String errorMessage) {
        super(errorMessage);
    }
}
