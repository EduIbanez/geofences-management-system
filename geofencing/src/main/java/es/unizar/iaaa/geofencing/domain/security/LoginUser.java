package es.unizar.iaaa.geofencing.domain.security;

import org.hibernate.validator.constraints.NotEmpty;

public class LoginUser {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    public LoginUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
