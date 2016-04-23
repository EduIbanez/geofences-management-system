package es.unizar.iaaa.geofencing.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.vividsolutions.jts.geom.Geometry;

import org.hibernate.annotations.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import es.unizar.iaaa.geofencing.view.View;

@Entity
@Table(name="GEOFENCES")
public class Geofence {

    private Long id;
    private String type;

    private Map<String, String> properties = new HashMap<>();

    @Type(type="org.hibernate.spatial.GeometryType")
    private Geometry geometry;
    private User user;
    private Set<Rule> rules;

    public Geofence() {}

    public Geofence(@JsonProperty("id") Long id, @JsonProperty("type") String type,
                    @JsonProperty("properties") Map<String, String> properties, @JsonProperty("geometry") Geometry geometry,
                    @JsonProperty("user") User user, @JsonProperty("rules") Set<Rule> rules) {
        this.id = id;
        this.type = type;
        this.properties = properties;
        this.geometry = geometry;
        this.user = user;
        this.rules = rules;
    }

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    @JsonView(View.GeofenceBaseView.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "TYPE", nullable = false, length = 20)
    @JsonView(View.GeofenceBaseView.class)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="PROPERTIES_KEY")
    @Column(name="PROPERTIES_VALUE")
    @CollectionTable(name="PROPERTIES_MAPPING", joinColumns=@JoinColumn(name="GEOFENCE_ID"))
    @JsonView(View.GeofenceBaseView.class)
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Column(name = "GEOMETRY", nullable = false, length = 100)
    @Type(type = "org.hibernate.spatial.GeometryType")
    @JsonView(View.GeofenceBaseView.class)
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(View.GeofenceCompleteView.class)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "geofence")
    @JsonView(View.GeofenceCompleteView.class)
    public Set<Rule> getRules() {
        return rules;
    }

    public void setRules(Set<Rule> rules) {
        this.rules = rules;
    }

    public String toString() {
        return "Geofence(id: "+id+" type: "+type+" properties: "+properties+" geom: "+geometry+" user id: "+user.getId()+
                "rules"+rules+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Geofence geofence = (Geofence) o;
        return id == geofence.id &&
                Objects.equals(type, geofence.type) &&
                Objects.equals(properties, geofence.properties) &&
                Objects.equals(geometry, geofence.geometry) &&
                Objects.equals(user.getId(), geofence.getUser().getId()) &&
                Objects.equals(rules, geofence.rules);
    }
}