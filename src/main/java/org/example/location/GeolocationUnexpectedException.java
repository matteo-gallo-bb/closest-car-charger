package org.example.location;

public class GeolocationUnexpectedException extends RuntimeException {

    public GeolocationUnexpectedException(Exception cause) {
        super(cause);
    }
}
