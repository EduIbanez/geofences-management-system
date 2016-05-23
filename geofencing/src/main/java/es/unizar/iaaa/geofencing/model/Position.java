package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.vividsolutions.jts.geom.Geometry;
import es.unizar.iaaa.geofencing.view.View;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name="POSITIONS")
public class Position {

    private Long id;
    private Geometry coordinates;
    private Time time;
    private User user;

    public Position(){}

    public Position(@JsonProperty("id") Long id, @JsonProperty("coordinates") Geometry coordinates,
                    @JsonProperty("time") Time time, @JsonProperty("user") User user) {
        this.id = id;
        this.coordinates = coordinates;
        this.time = time;
        this.user = user;
    }

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    @JsonView(View.NotificationBaseView.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "COORDINATES", nullable = false, length = 80)
    @Type(type = "org.hibernate.spatial.GeometryType")
    public Geometry getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Geometry coordinates) {
        this.coordinates = coordinates;
    }

    @Column(name = "TIME", nullable = false, length = 15)
    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toString() {
        return "Day(id: "+id+" coordinates: "+coordinates+" time: "+time+" user: "+user+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return id == position.id &&
                Objects.equals(coordinates, position.coordinates) &&
                Objects.equals(time, position.time) &&
                Objects.equals(user, position.user);
    }
}
