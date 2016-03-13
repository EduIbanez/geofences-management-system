package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Geometry {

    private String type;
    private ArrayList<Double[]> coordinates;

    public Geometry(@JsonProperty("type") String type, @JsonProperty("coordinates") ArrayList<Double[]> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Double[]> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Double[]> coordinates) {
        this.coordinates = coordinates;
    }
}
