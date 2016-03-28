package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Set;

public class User {

    private long id;
    private String email;
    private String pass;
    private String first_name;
    private String last_name;
    private String birthday;
    private String imei;
    private Set<Geofence> geofences;

    public User(@JsonProperty("id") long id, @JsonProperty("email") String email,
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
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "EMAIL", unique = true, nullable = false, length = 30)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "PASS", unique = false, nullable = false, length = 30)
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Column(name = "FIRST_NAME", unique = false, nullable = false, length = 30)
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    @Column(name = "LAST_NAME", unique = false, nullable = false, length = 30)
    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    @Column(name = "BIRTHDAY", unique = false, nullable = false, length = 10)
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
}