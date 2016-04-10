package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Embeddable;

import es.unizar.iaaa.geofencing.view.View;

import java.util.Map;

@Embeddable
public class Properties {

    private Map<String, Object> properties;

    public Properties(){

    }

    public Properties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @JsonView(View.Geofence.class)
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String toString() {
        return "Properties(properties: "+properties.toString()+")";
    }
}
