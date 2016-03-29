package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Properties {

    private String name;

    public Properties(@JsonProperty("name") String name) {
        this.name = name;
    }

    @Column(name = "NAME", unique = false, nullable = false, length = 30)
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
