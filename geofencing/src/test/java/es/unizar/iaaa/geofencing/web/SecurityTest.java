package es.unizar.iaaa.geofencing.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.HashSet;

import es.unizar.iaaa.geofencing.Application;
import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest("server.port:0")
public class SecurityTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String PASSWORD = "password";

    private static final User USER1 = new User(null, "example.gmail.com", PASSWORD, "First", "Last", Date.valueOf("1992-08-07"),
            "356938035643809", new HashSet<>(), true, "user", new HashSet<>(), new HashSet<>());

    @Before
    public void setup() {
        userRepository.deleteAll();
        USER1.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(USER1);
    }
    @Test
    public void autheorise() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", "user");
        map.add("password", "password");
        RestTemplate client = new RestTemplate();
        ResponseEntity<String> response = client.postForEntity("http://localhost:{port}/login", map, String.class, port);
    }

}
