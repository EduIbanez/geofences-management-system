package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class Geofence {

    private long id;
    private String type;
    private Properties properties;

    @Type(type="org.hibernate.spatial.GeometryType")
    private Geometry geometry;
    private User user;

    public Geofence(@JsonProperty("id") long id, @JsonProperty("type") String type,
                    @JsonProperty("properties") Properties properties, @JsonProperty("geometry") Geometry geometry,
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
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "TYPE", unique = false, nullable = false, length = 20)
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

    @Column(name = "GEOMETRY", unique = false, nullable = false, length = 100)
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toString() {
        return "Geofence(id: "+id+" type: "+type+" properties: "+properties+" geom: "+geometry+" user: "+user+")";
    }
}