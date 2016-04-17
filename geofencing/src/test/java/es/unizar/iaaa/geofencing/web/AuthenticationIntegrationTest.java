package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.iaaa.geofencing.Application;
import es.unizar.iaaa.geofencing.domain.hmac.HmacSigner;
import es.unizar.iaaa.geofencing.domain.hmac.HmacUtils;
import es.unizar.iaaa.geofencing.domain.security.AuthenticatedUser;
import es.unizar.iaaa.geofencing.domain.security.LoginUser;
import es.unizar.iaaa.geofencing.domain.security.Profile;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class AuthenticationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    /**
     * Authenticate a user with its credentials.
     *
     * @param email    username
     * @param password password
     * @throws Exception
     */
    public MvcResult authenticate(String email, String password,int status) throws Exception {
        LoginUser loginUser = new LoginUser(email, password);
        MvcResult result = mockMvc.perform(post("/api/users/authenticate", false)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().is(status)).andReturn();

        Assert.assertNotNull(result);

        if(result.getResponse().getStatus() == 200) {
            Assert.assertTrue(!result.getResponse().getHeader(HmacUtils.X_SECRET).isEmpty());
            Assert.assertTrue(!result.getResponse().getHeader(HmacUtils.X_TOKEN_ACCESS).isEmpty());
        }

        return result;
    }

    public void logout(MvcResult result,int status) throws Exception{

        String secret = new String(Base64.decodeBase64(result.getResponse().getHeader(HmacUtils.X_SECRET).trim().getBytes()));
        String jwtToken = result.getResponse().getHeader(HmacUtils.X_TOKEN_ACCESS);
        String date = DateTime.now().toDateTimeISO().toString();
        String message = "GEThttp://localhost:8888/api/users/logout" + date;
        String digest = HmacSigner.encodeMac(secret, message, HmacUtils.HMAC_SHA_256);

        mockMvc.perform(get("/api/users/logout", false)
                .header(HmacUtils.AUTHENTICATION, jwtToken)
                .header(HmacUtils.X_DIGEST, digest)
                .header(HmacUtils.X_ONCE, date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(status)).andReturn();
    }

    @Test
    public void authenticationAdminSuccess() throws Exception {
        MvcResult result = authenticate("admin@gmail.com", "password", 200);
        AuthenticatedUser authenticatedUser = objectMapper.readValue(result.getResponse().getContentAsString(), AuthenticatedUser.class);
        Assert.assertNotNull(authenticatedUser);
        Assert.assertNotNull(authenticatedUser.getEmail());
        Assert.assertNull(authenticatedUser.getPassword());
        Assert.assertNotNull(authenticatedUser.getAuthorities());
        Assert.assertNotNull(authenticatedUser.getProfile());
        Assert.assertEquals(authenticatedUser.getProfile(), Profile.ADMIN);
    }

    @Test
    public void authenticationUserSuccess() throws Exception {
        MvcResult result = authenticate("user@gmail.com", "password", 200);
        AuthenticatedUser authenticatedUser = objectMapper.readValue(result.getResponse().getContentAsString(), AuthenticatedUser.class);
        Assert.assertNotNull(authenticatedUser);
        Assert.assertNotNull(authenticatedUser.getEmail());
        Assert.assertNull(authenticatedUser.getPassword());
        Assert.assertNotNull(authenticatedUser.getAuthorities());
        Assert.assertNotNull(authenticatedUser.getProfile());
        Assert.assertEquals(authenticatedUser.getProfile(), Profile.USER);
    }

    @Test
    public void badCredentials() throws Exception {
        authenticate("user@gmail.com", "wrongPassword", 403);
    }

    @Test
    public void logoutSuccess() throws Exception {
        MvcResult result = authenticate("admin@gmail.com", "password", 200);
        logout(result, 200);
    }
}
