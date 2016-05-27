package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Geometry;
import java.util.*;

public class Position {

    private Geometry coordinates;

    private Map<Geofence, GeofenceRegistry> entering = new HashMap<>();
    private Set<Geofence> entering_discarded = new HashSet<>();

    private Map<Geofence, GeofenceRegistry> leaving_before = new HashMap<>();
    private Map<Geofence, GeofenceRegistry> leaving_now = new HashMap<>();

    private Map<Geofence, GeofenceRegistry> inside_before = new HashMap<>();
    private Map<Geofence, GeofenceRegistry> inside_now = new HashMap<>();
    private Set<Geofence> inside_discarded = new HashSet<>();

    public Position(){}

    public Position(@JsonProperty("coordinates") Geometry coordinates,
                    @JsonProperty("entering") Map<Geofence, GeofenceRegistry> entering,
                    @JsonProperty("entering_discarded") Set<Geofence> entering_discarded,
                    @JsonProperty("leaving_before") Map<Geofence, GeofenceRegistry> leaving_before,
                    @JsonProperty("leaving_now") Map<Geofence, GeofenceRegistry> leaving_now,
                    @JsonProperty("inside_before") Map<Geofence, GeofenceRegistry> inside_before,
                    @JsonProperty("inside_now") Map<Geofence, GeofenceRegistry> inside_now,
                    @JsonProperty("inside_discarded") Set<Geofence> inside_discarded) {
        this.coordinates = coordinates;
        this.entering = entering;
        this.entering_discarded = entering_discarded;
        this.leaving_before = leaving_before;
        this.leaving_now = leaving_now;
        this.inside_before = inside_before;
        this.inside_now = inside_now;
        this.inside_discarded = inside_discarded;
    }

    public Geometry getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Geometry coordinates) {
        this.coordinates = coordinates;
    }

    public Map<Geofence, GeofenceRegistry> getEntering() {
        return entering;
    }

    public void setEntering(Map<Geofence, GeofenceRegistry> entering) {
        this.entering = entering;
    }

    public Set<Geofence> getEnteringDiscarded() {
        return entering_discarded;
    }

    public void setEnteringDiscarded(Set<Geofence> entering_discarded) {
        this.entering_discarded = entering_discarded;
    }

    public Map<Geofence, GeofenceRegistry> getLeavingBefore() {
        return leaving_before;
    }

    public void setLeavingBefore(Map<Geofence, GeofenceRegistry> leaving_before) {
        this.leaving_before = leaving_before;
    }

    public Map<Geofence, GeofenceRegistry> getLeavingNow() {
        return leaving_now;
    }

    public void setLeavingNow(Map<Geofence, GeofenceRegistry> leaving_now) {
        this.leaving_now = leaving_now;
    }

    public Map<Geofence, GeofenceRegistry> getInsideBefore() {
        return inside_before;
    }

    public void setInsideBefore(Map<Geofence, GeofenceRegistry> inside_before) {
        this.inside_before = inside_before;
    }

    public Map<Geofence, GeofenceRegistry> getInsideNow() {
        return inside_now;
    }

    public void setInsideNow(Map<Geofence, GeofenceRegistry> inside_now) {
        this.inside_now = inside_now;
    }

    public Set<Geofence> getInsideDiscarded() {
        return inside_discarded;
    }

    public void setInsideDiscarded(Set<Geofence> inside_discarded) {
        this.inside_discarded = inside_discarded;
    }
}
