package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Geofence {

    private int id;
    private String type;
    private Properties properties;
    private Geometry geometry;

    public Geofence(@JsonProperty("id") int id, @JsonProperty("type") String type,
                    @JsonProperty("properties") Properties properties, @JsonProperty("geometry") Geometry geometry) {
        this.id = id;
        this.type = type;
        this.properties = properties;
        this.geometry = geometry;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}