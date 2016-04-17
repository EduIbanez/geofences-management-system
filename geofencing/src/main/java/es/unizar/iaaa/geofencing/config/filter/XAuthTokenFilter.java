package es.unizar.iaaa.geofencing.config.filter;

import es.unizar.iaaa.geofencing.hmac.HmacException;
import es.unizar.iaaa.geofencing.hmac.HmacSigner;
import es.unizar.iaaa.geofencing.hmac.HmacUtils;
import es.unizar.iaaa.geofencing.domain.security.AuthenticatedUser;
import es.unizar.iaaa.geofencing.mock.MockUsers;
import es.unizar.iaaa.geofencing.service.AuthenticationService;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XAuthTokenFilter extends GenericFilterBean {

    private AuthenticationService authenticationService;

    public XAuthTokenFilter(AuthenticationService authenticationService){
       this.authenticationService = authenticationService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!request.getRequestURI().contains("/api") || request.getRequestURI().contains("/api/authenticate")){
            filterChain.doFilter(request, response);
        } else {

            try {
                String jwtHeader = request.getHeader(HmacUtils.AUTHENTICATION);
                String userId = HmacSigner.getJwtIss(jwtHeader);

                // Retrieve user in cache
                AuthenticatedUser authenticatedUser = MockUsers.findById(Long.valueOf(userId));
                Assert.notNull(authenticatedUser,"No user found with id: "+userId);
                this.authenticationService.tokenAuthentication(authenticatedUser.getEmail());
                filterChain.doFilter(request,response);
            } catch (HmacException e) {
                e.printStackTrace();
            }
        }

    }
}
