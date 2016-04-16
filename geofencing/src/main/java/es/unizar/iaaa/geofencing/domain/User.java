package es.unizar.iaaa.geofencing.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import es.unizar.iaaa.geofencing.view.View;

@Entity
@Table(name="USER_")
public class User {

    private Long id;
    private String email;
    private String pass;
    private String first_name;
    private String last_name;
    private String birthday;
    private String imei;
    private Set<Geofence> geofences;

    public User(){}

    public User(@JsonProperty("id") Long id, @JsonProperty("email") String email,
                @JsonProperty("pass") String pass, @JsonProperty("first_name") String first_name,
                @JsonProperty("last_name") String last_name, @JsonProperty("birthday") String birthday,
                @JsonProperty("imei") String imei, @JsonProperty("geofence") Set<Geofence> geofences) {
        this.id = id;
        this.email = email;
        this.pass = pass;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birthday = birthday;
        this.imei = imei;
        this.geofences = geofences;
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

    @Column(name = "EMAIL", unique = true, nullable = false, length = 30)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "PASS", nullable = false, length = 30)
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Column(name = "FIRST_NAME", nullable = false, length = 30)
    @JsonView(View.Geofence.class)
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    @Column(name = "LAST_NAME", nullable = false, length = 30)
    @JsonView(View.Geofence.class)
    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    @Column(name = "BIRTHDAY", nullable = false, length = 10)
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Column(name = "IMEI", unique = true, nullable = false, length = 15)
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public Set<Geofence> getGeofences() {
        return geofences;
    }

    public void setGeofences(Set<Geofence> geofences) {
        this.geofences = geofences;
    }

    public String toString() {
        return "User(id: "+id+" email: "+email+" pass: "+pass+" first_name: "+first_name+
                "last_name: "+last_name+" birthday: "+birthday+" imei: "+imei+" geofence: "+geofences+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(email, user.email) &&
                Objects.equals(pass, user.pass) &&
                Objects.equals(first_name, user.first_name) &&
                Objects.equals(last_name, user.last_name) &&
                Objects.equals(birthday, user.birthday) &&
                Objects.equals(imei, user.imei) &&
                Objects.equals(geofences, user.geofences);
    }
}