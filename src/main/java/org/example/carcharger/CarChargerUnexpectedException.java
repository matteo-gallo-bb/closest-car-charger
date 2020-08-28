package org.example.carcharger;

public class CarChargerUnexpectedException extends RuntimeException {

    public CarChargerUnexpectedException(Exception cause) {
        super(cause);
    }
}
