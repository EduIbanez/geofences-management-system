package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import es.unizar.iaaa.geofencing.view.View;

@Embeddable
public class Properties {

    private String name;

    public Properties(){

    }

    public Properties(@JsonProperty("name") String name) {
        this.name = name;
    }

    @Column(name = "NAME", unique = false, nullable = false, length = 30)
    @JsonView(View.Geofence.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Properties(name: "+name+")";
    }
}
