package org.example.carcharger;

import java.util.Objects;
import java.util.StringJoiner;

public class CarCharger {

    // Distance is expressed in Km
    private final String name;
    private final float distance;
    private final String address;
    private final int numberOfPoints;

    public CarCharger(String name, float distance, String address, int numberOfPoints) {
        this.name = name;
        this.distance = distance;
        this.address = address;
        this.numberOfPoints = numberOfPoints;
    }

    public String getName() {
        return name;
    }

    public float getDistance() {
        return distance;
    }

    public String getAddress() {
        return address;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarCharger that = (CarCharger) o;
        return Float.compare(that.distance, distance) == 0 &&
                numberOfPoints == that.numberOfPoints &&
                name.equals(that.name) &&
                address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, distance, address, numberOfPoints);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CarCharger.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("distance=" + distance)
                .add("address='" + address + "'")
                .add("numberOfPoints=" + numberOfPoints)
                .toString();
    }
}
