package es.unizar.iaaa.geofencing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/*").fullyAuthenticated()
                .antMatchers(HttpMethod.DELETE, "/api/users/*").fullyAuthenticated()
                .antMatchers(HttpMethod.GET, "/api/users/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/geofences").fullyAuthenticated()
                .antMatchers(HttpMethod.GET, "/api/geofences").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/geofences/*").fullyAuthenticated()
                .antMatchers(HttpMethod.DELETE, "/api/geofences/*").fullyAuthenticated()
                .antMatchers(HttpMethod.GET, "/api/geofences/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/rules").fullyAuthenticated()
                .antMatchers(HttpMethod.PUT, "/api/rules/*").fullyAuthenticated()
                .antMatchers(HttpMethod.DELETE, "/api/rules/*").fullyAuthenticated()
                .antMatchers(HttpMethod.GET, "/api/rules/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/notifications").fullyAuthenticated()
                .antMatchers(HttpMethod.GET, "/api/notifications").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/notifications/*").fullyAuthenticated()
                .antMatchers(HttpMethod.DELETE, "/api/notifications/*").fullyAuthenticated()
                .antMatchers(HttpMethod.GET, "/api/notifications/*").permitAll()
                .antMatchers("/api/locations/**").permitAll()
                .anyRequest().denyAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("SELECT email, password, enabled FROM users WHERE email = ?")
                .authoritiesByUsernameQuery("SELECT email, role FROM users WHERE email = ?")
                .passwordEncoder(passwordEncoder());
    }
}
