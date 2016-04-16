package es.unizar.iaaa.geofencing.config;

import es.unizar.iaaa.geofencing.config.filter.HmacSecurityFilter;
import es.unizar.iaaa.geofencing.config.filter.XAuthTokenFilter;
import es.unizar.iaaa.geofencing.domain.hmac.HmacRequester;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

public class HmacSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private HmacRequester hmacRequester;

    public HmacSecurityConfig(HmacRequester hmacRequester){
        this.hmacRequester = hmacRequester;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        HmacSecurityFilter hmacSecurityFilter = new HmacSecurityFilter(hmacRequester);

        // Trigger this filter before SpringSecurity authentication validator
        builder.addFilterBefore(hmacSecurityFilter, XAuthTokenFilter.class);
    }
}
