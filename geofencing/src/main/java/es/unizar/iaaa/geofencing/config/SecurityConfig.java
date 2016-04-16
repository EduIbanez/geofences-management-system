package es.unizar.iaaa.geofencing.config;

import es.unizar.iaaa.geofencing.domain.hmac.HmacRequester;
import es.unizar.iaaa.geofencing.domain.security.AuthenticatedUser;
import es.unizar.iaaa.geofencing.mock.MockUsers;
import es.unizar.iaaa.geofencing.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private HmacRequester hmacRequester;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/api/users/authenticate").anonymous()
                    .antMatchers("/api/**").authenticated()
                .and()
                .csrf()
                    .disable()
                    .headers()
                    .frameOptions().disable()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout()
                    .permitAll()
                .and()
                    .apply(authTokenConfig())
                .and()
                    .apply(hmacSecurityConfig());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        InMemoryUserDetailsManagerConfigurer configurer = auth
                .inMemoryAuthentication()
                    .passwordEncoder(passwordEncoder());

        for(AuthenticatedUser authenticatedUser : MockUsers.getUsers()) {
            configurer.withUser(authenticatedUser.getEmail())
                    .password(passwordEncoder().encode(authenticatedUser.getPassword()))
                    .roles(authenticatedUser.getProfile().name());
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private HmacSecurityConfig hmacSecurityConfig(){
        return new HmacSecurityConfig(hmacRequester);
    }

    private XAuthTokenConfig authTokenConfig(){
        return new XAuthTokenConfig(authenticationService);
    }
}
