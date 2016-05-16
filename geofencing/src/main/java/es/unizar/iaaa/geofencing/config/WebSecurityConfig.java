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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.sql.DataSource;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(OPTIONS, "/*/**").permitAll()
                .antMatchers(POST, "/api/users").permitAll()
                .antMatchers(GET, "/api/users/*").permitAll()
                .antMatchers(GET, "/api/geofences").permitAll()
                .antMatchers(GET, "/api/geofences/*").permitAll()
                .antMatchers(GET, "/api/rules/*").permitAll()
                .antMatchers(GET, "/api/notifications/*").permitAll()
                .antMatchers(GET, "/login").permitAll()
                .antMatchers(GET, "/logout").authenticated()
                .antMatchers(PUT, "/api/users/*").authenticated()
                .antMatchers(DELETE, "/api/users/*").authenticated()
                .antMatchers(POST, "/api/geofences").authenticated()
                .antMatchers(PUT, "/api/geofences/*").authenticated()
                .antMatchers(DELETE, "/api/geofences/*").authenticated()
                .antMatchers(POST, "/api/rules").authenticated()
                .antMatchers(PUT, "/api/rules/*").authenticated()
                .antMatchers(DELETE, "/api/rules/*").authenticated()
                .antMatchers(POST, "/api/notifications").authenticated()
                .antMatchers(GET, "/api/notifications").authenticated()
                .antMatchers(PUT, "/api/notifications/*").authenticated()
                .antMatchers(DELETE, "/api/notifications/*").authenticated()
                .antMatchers(POST, "/api/locations/**").authenticated()
                .antMatchers(GET, "/api/locations/**").authenticated();

        // Handlers and entry points
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.formLogin().failureHandler(authenticationFailureHandler);

        // LOGIN
        http.formLogin().loginProcessingUrl("/login").successHandler(authenticationSuccessHandler);

        // LOGOUT
        http.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);

        // CSRF
        http.csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("SELECT email as username, password, enabled FROM users WHERE email = ?")
                .authoritiesByUsernameQuery("SELECT email as username, role as authority FROM users WHERE email = ?")
                .passwordEncoder(passwordEncoder());
    }
}
