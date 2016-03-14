package es.unizar.iaaa.geofencing.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private int id;
    private String email;
    private String pass;
    private String first_name;
    private String last_name;
    private String birthday;
    private String imei;

    public User(@JsonProperty("id")int id, @JsonProperty("email") String email,
                @JsonProperty("pass") String pass, @JsonProperty("first_name") String first_name,
                @JsonProperty("last_name") String last_name, @JsonProperty("birthday") String birthday,
                @JsonProperty("imei") String imei) {
        this.id = id;
        this.email = email;
        this.pass = pass;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birthday = birthday;
        this.imei = imei;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}