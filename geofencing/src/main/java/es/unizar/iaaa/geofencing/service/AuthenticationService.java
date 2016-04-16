package es.unizar.iaaa.geofencing.service;

import es.unizar.iaaa.geofencing.config.filter.HmacSecurityFilter;
import es.unizar.iaaa.geofencing.domain.hmac.HmacException;
import es.unizar.iaaa.geofencing.domain.hmac.HmacSigner;
import es.unizar.iaaa.geofencing.domain.hmac.HmacToken;
import es.unizar.iaaa.geofencing.domain.hmac.HmacUtils;
import es.unizar.iaaa.geofencing.domain.security.AuthenticatedUser;
import es.unizar.iaaa.geofencing.domain.security.LoginUser;
import es.unizar.iaaa.geofencing.domain.security.SecurityUser;
import es.unizar.iaaa.geofencing.mock.MockUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Authenticate a user in Spring Security
     * The following headers are set in the response:
     * - X-TokenAccess: JWT
     * - X-Secret: Generated secret in base64 using SHA-256 algorithm
     * - WWW-Authenticate: Used algorithm to encode secret
     * The authenticated user in set ine the Spring Security context
     * The generated secret is stored in a static list for every user
     * @param loginUser credentials
     * @param response http response
     * @return AuthenticatedUser instance
     * @throws HmacException
     */
    public AuthenticatedUser authenticate(LoginUser loginUser, HttpServletResponse response) throws HmacException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Retrieve security user after authentication
        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(loginUser.getEmail());

        // Parse Granted authorities to a list of string authorities
        List<String> authorities = new ArrayList<>();
        for(GrantedAuthority authority : securityUser.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }

        // Get Hmac signed token
        Map<String,String> customClaims = new HashMap<>();
        customClaims.put(HmacSigner.ENCODING_CLAIM_PROPERTY, HmacUtils.HMAC_SHA_256);

        // Generate a random secret
        String secret = HmacSigner.generateSecret();

        HmacToken hmacToken = HmacSigner.getSignedToken(secret, String.valueOf(securityUser.getId()), HmacSecurityFilter.JWT_TTL, customClaims);

        for(AuthenticatedUser authenticatedUser : MockUsers.getUsers()) {
            if(authenticatedUser.getId().equals(securityUser.getId())) {
                authenticatedUser.setSecretKey(secret);
            }
        }

        // Set all tokens in http response headers
        response.setHeader(HmacUtils.X_TOKEN_ACCESS, hmacToken.getJwt());
        response.setHeader(HmacUtils.X_SECRET, hmacToken.getSecret());
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, HmacUtils.HMAC_SHA_256);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(securityUser.getId(), securityUser.getUsername(),
                authorities, securityUser.getProfile());
        return authenticatedUser;
    }

    /**
     * Logout a user
     * - Clear the Spring Security context
     * - Remove the stored AuthenticatedUser secret
     */
    public void logout(){
        if(SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            AuthenticatedUser authenticatedUser = MockUsers.findById(securityUser.getId());
            if(authenticatedUser != null) {
                authenticatedUser.setSecretKey(null);
            }

        }
    }

    /**
     * Authentication for every request
     * - Triggered by every http request except the authentication
     * Set the authenticated user in the Spring Security context
     * @param username username
     */
    public void tokenAuthentication(String username){
        UserDetails details = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
