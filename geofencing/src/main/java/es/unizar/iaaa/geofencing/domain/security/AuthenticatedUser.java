package es.unizar.iaaa.geofencing.domain.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticatedUser {

    private Long id;

    @NotEmpty
    private String email;

    @JsonIgnore
    private String password;

    @NotEmpty
    private List<String> authorities;

    @JsonIgnore
    private String secretKey;

    private Profile profile;

    public AuthenticatedUser(Long id, String email, String password, List<String> authorities, Profile profile) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.profile = profile;
    }

    public AuthenticatedUser(Long id, String email, List<String> authorities, Profile profile) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
        this.profile = profile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
