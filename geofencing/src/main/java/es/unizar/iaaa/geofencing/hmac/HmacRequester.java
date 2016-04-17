package es.unizar.iaaa.geofencing.hmac;

import javax.servlet.http.HttpServletRequest;

public interface HmacRequester {

    /**
     * Check if its possible to verify the request
     * @param request http request
     * @return true if possible, false otherwise
     */
    Boolean canVerify(HttpServletRequest request);

    /**
     * Get the stored secret
     * @param iss issuer
     * @return secret key
     */
    String getSecret(String iss);

    /**
     * Is the secret encoded in base 64
     * @return true if encoded in base 64, false otherwise
     */
    Boolean isSecretInBase64();
}
