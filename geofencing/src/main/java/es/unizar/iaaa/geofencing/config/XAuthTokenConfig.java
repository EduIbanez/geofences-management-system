package es.unizar.iaaa.geofencing.config;

import es.unizar.iaaa.geofencing.config.filter.XAuthTokenFilter;
import es.unizar.iaaa.geofencing.service.AuthenticationService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class XAuthTokenConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private AuthenticationService authenticationService;

    public XAuthTokenConfig(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        XAuthTokenFilter xAuthTokenFilter = new XAuthTokenFilter(authenticationService);

        // Trigger this filter before SpringSecurity authentication validator
        builder.addFilterBefore(xAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
