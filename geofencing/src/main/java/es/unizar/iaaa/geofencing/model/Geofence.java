package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import es.unizar.iaaa.geofencing.view.View;

import java.util.Map;

@Entity
@Table(name="GEOFENCE")
public class Geofence {

    private Long id;
    private String type;
    private Map<String, Object> properties;

    @Type(type="org.hibernate.spatial.GeometryType")
    private Geometry geometry;
    private User user;

    public Geofence() {}

    public Geofence(@JsonProperty("id") Long id, @JsonProperty("type") String type,
                    @JsonProperty("properties") Map<String, Object> properties, @JsonProperty("geometry") Geometry geometry,
                    @JsonProperty("user") User user) {
        this.id = id;
        this.type = type;
        this.properties = properties;
        this.geometry = geometry;
        this.user = user;
    }

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    @JsonView(View.Geofence.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "TYPE", nullable = false, length = 20)
    @JsonView(View.Geofence.class)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonView(View.Geofence.class)
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Column(name = "GEOMETRY", nullable = false, length = 100)
    @Type(type = "org.hibernate.spatial.GeometryType")
    @JsonView(View.Geofence.class)
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(View.Geofence.class)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toString() {
        return "Geofence(id: "+id+" type: "+type+" properties: "+properties.toString()+" geom: "+geometry+" user id: "+user.getId()+")";
    }
}