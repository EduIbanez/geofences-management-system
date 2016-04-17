package es.unizar.iaaa.geofencing.service;

import es.unizar.iaaa.geofencing.hmac.HmacRequester;
import es.unizar.iaaa.geofencing.domain.security.AuthenticatedUser;
import es.unizar.iaaa.geofencing.mock.MockUsers;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class DefaultHmacRequester implements HmacRequester {

    @Override
    public Boolean canVerify(HttpServletRequest request) {
        return request.getRequestURI().contains("/api") && !request.getRequestURI().contains("/api/users/authenticate");
    }

    @Override
    public String getSecret(String iss) {
        AuthenticatedUser authenticatedUser = MockUsers.findById(Long.valueOf(iss));
        if(authenticatedUser != null){
            return authenticatedUser.getSecretKey();
        }
        return null;
    }

    @Override
    public Boolean isSecretInBase64() {
        return true;
    }
}
