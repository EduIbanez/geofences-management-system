package es.unizar.iaaa.geofencing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(POST, "/api/users").permitAll()
                .antMatchers(PUT, "/api/users/*").fullyAuthenticated()
                .antMatchers(DELETE, "/api/users/*").fullyAuthenticated()
                .antMatchers(GET, "/api/users/*").permitAll()
                .antMatchers(POST, "/api/geofences").fullyAuthenticated()
                .antMatchers(GET, "/api/geofences").permitAll()
                .antMatchers(PUT, "/api/geofences/*").fullyAuthenticated()
                .antMatchers(DELETE, "/api/geofences/*").fullyAuthenticated()
                .antMatchers(GET, "/api/geofences/*").permitAll()
                .antMatchers(POST, "/api/rules").fullyAuthenticated()
                .antMatchers(PUT, "/api/rules/*").fullyAuthenticated()
                .antMatchers(DELETE, "/api/rules/*").fullyAuthenticated()
                .antMatchers(GET, "/api/rules/*").permitAll()
                .antMatchers(POST, "/api/notifications").fullyAuthenticated()
                .antMatchers(GET, "/api/notifications").fullyAuthenticated()
                .antMatchers(PUT, "/api/notifications/*").fullyAuthenticated()
                .antMatchers(DELETE, "/api/notifications/*").fullyAuthenticated()
                .antMatchers(GET, "/api/notifications/*").permitAll()
                .antMatchers(POST, "/api/locations/**").fullyAuthenticated()
                .antMatchers(GET, "/api/locations/**").fullyAuthenticated()
                .antMatchers(OPTIONS, "/api/**").permitAll()
                .anyRequest().denyAll()
                .and()
                    .formLogin().loginProcessingUrl("/api/users/login").permitAll()
                .and()
                    .logout().permitAll()
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
