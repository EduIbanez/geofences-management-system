package es.unizar.iaaa.geofencing.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import es.unizar.iaaa.geofencing.view.View;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="RULES")
public class Rule {

    private Long id;
    private Boolean enabled;
    private Boolean entering;
    private Boolean leaving;
    private Boolean inside;
    private Integer time_entering;
    private Integer time_leaving;
    private Integer time_inside;
    private Set<Geofence> geofences;

    public Rule(){}

    public Rule(@JsonProperty("id") Long id, @JsonProperty("enabled") Boolean enabled,
                @JsonProperty("entering") Boolean entering, @JsonProperty("leaving") Boolean leaving,
                @JsonProperty("inside") Boolean inside, @JsonProperty("time_entering") Integer time_entering,
                @JsonProperty("time_leaving") Integer time_leaving, @JsonProperty("time_inside") Integer time_inside,
                @JsonProperty("geofence") Set<Geofence> geofences) {
        this.id = id;
        this.enabled = enabled;
        this.entering = entering;
        this.leaving = leaving;
        this.inside = inside;
        this.time_entering = time_entering;
        this.time_leaving = time_leaving;
        this.time_inside = time_inside;
        this.geofences = geofences;
    }

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    @JsonView(View.RuleBaseView.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "ENABLED", nullable = false, length = 5)
    @JsonView(View.RuleBaseView.class)
    public Boolean getEnabled() {
        return enabled;
    }

    public void getEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Column(name = "ENTERING", nullable = false, length = 5)
    @JsonView(View.RuleBaseView.class)
    public Boolean getEntering() {
        return entering;
    }

    public void setEntering(Boolean entering) {
        this.entering = entering;
    }

    @Column(name = "LEAVING", nullable = false, length = 5)
    @JsonView(View.RuleBaseView.class)
    public Boolean getLeaving() {
        return leaving;
    }

    public void setLeaving(Boolean leaving) {
        this.leaving = leaving;
    }

    @Column(name = "INSIDE", nullable = false, length = 5)
    @JsonView(View.RuleBaseView.class)
    public Boolean getInside() {
        return inside;
    }

    public void setInside(Boolean inside) {
        this.inside = inside;
    }

    @Column(name = "TIME_ENTERING", nullable = false, length = 5)
    @JsonView(View.RuleBaseView.class)
    public Integer getTimeEntering() {
        return time_entering;
    }

    public void setTimeEntering(Integer time_entering) {
        this.time_entering = time_entering;
    }

    @Column(name = "TIME_LEAVING", nullable = false, length = 5)
    @JsonView(View.RuleBaseView.class)
    public Integer getTimeLeaving() {
        return time_leaving;
    }

    public void setTimeLeaving(Integer time_leaving) {
        this.time_leaving = time_leaving;
    }

    @Column(name = "TIME_INSIDE", nullable = false, length = 5)
    @JsonView(View.RuleBaseView.class)
    public Boolean getTimeInside() {
        return inside;
    }

    public void setTimeInside(Integer time_inside) {
        this.time_inside = time_inside;
    }

    @JsonView(View.RuleCompleteView.class)
    public Set<Geofence> getGeofences() {
        return geofences;
    }

    public void setGeofences(Set<Geofence> geofences) {
        this.geofences = geofences;
    }

    public String toString() {
        return "Rule(id: "+id+" enabled: "+enabled+" entering: "+entering+" leaving: "+leaving+
                " inside: "+inside+" time_entering: "+time_entering+" time_leaving: "+time_leaving+
                " time_inside: "+time_inside+" geofence: "+geofences+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule user = (Rule) o;
        return id == user.id &&
                Objects.equals(enabled, user.enabled) &&
                Objects.equals(entering, user.entering) &&
                Objects.equals(leaving, user.leaving) &&
                Objects.equals(inside, user.inside) &&
                Objects.equals(time_entering, user.time_entering) &&
                Objects.equals(time_leaving, user.time_leaving) &&
                Objects.equals(time_inside, user.time_inside) &&
                Objects.equals(geofences, user.geofences);
    }
}