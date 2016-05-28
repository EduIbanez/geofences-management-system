package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Geometry;
import java.util.*;

public class Position {

    private Geometry coordinates;

    private Map<Long, GeofenceRegistry> entering = new HashMap<>();
    private Set<Long> entering_discarded = new HashSet<>();

    private Map<Long, GeofenceRegistry> leaving_before = new HashMap<>();
    private Map<Long, GeofenceRegistry> leaving_now = new HashMap<>();

    private Map<Long, GeofenceRegistry> inside_before = new HashMap<>();
    private Map<Long, GeofenceRegistry> inside_now = new HashMap<>();
    private Set<Long> inside_discarded = new HashSet<>();

    public Position(){}

    public Position(@JsonProperty("coordinates") Geometry coordinates,
                    @JsonProperty("entering") Map<Long, GeofenceRegistry> entering,
                    @JsonProperty("entering_discarded") Set<Long> entering_discarded,
                    @JsonProperty("leaving_before") Map<Long, GeofenceRegistry> leaving_before,
                    @JsonProperty("leaving_now") Map<Long, GeofenceRegistry> leaving_now,
                    @JsonProperty("inside_before") Map<Long, GeofenceRegistry> inside_before,
                    @JsonProperty("inside_now") Map<Long, GeofenceRegistry> inside_now,
                    @JsonProperty("inside_discarded") Set<Long> inside_discarded) {
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

    public Map<Long, GeofenceRegistry> getEntering() {
        return entering;
    }

    public void setEntering(Map<Long, GeofenceRegistry> entering) {
        this.entering = entering;
    }

    public Set<Long> getEnteringDiscarded() {
        return entering_discarded;
    }

    public void setEnteringDiscarded(Set<Long> entering_discarded) {
        this.entering_discarded = entering_discarded;
    }

    public Map<Long, GeofenceRegistry> getLeavingBefore() {
        return leaving_before;
    }

    public void setLeavingBefore(Map<Long, GeofenceRegistry> leaving_before) {
        this.leaving_before = leaving_before;
    }

    public Map<Long, GeofenceRegistry> getLeavingNow() {
        return leaving_now;
    }

    public void setLeavingNow(Map<Long, GeofenceRegistry> leaving_now) {
        this.leaving_now = leaving_now;
    }

    public Map<Long, GeofenceRegistry> getInsideBefore() {
        return inside_before;
    }

    public void setInsideBefore(Map<Long, GeofenceRegistry> inside_before) {
        this.inside_before = inside_before;
    }

    public Map<Long, GeofenceRegistry> getInsideNow() {
        return inside_now;
    }

    public void setInsideNow(Map<Long, GeofenceRegistry> inside_now) {
        this.inside_now = inside_now;
    }

    public Set<Long> getInsideDiscarded() {
        return inside_discarded;
    }

    public void setInsideDiscarded(Set<Long> inside_discarded) {
        this.inside_discarded = inside_discarded;
    }
}
