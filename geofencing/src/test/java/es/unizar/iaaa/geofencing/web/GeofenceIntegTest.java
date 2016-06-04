package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import es.unizar.iaaa.geofencing.Application;
import es.unizar.iaaa.geofencing.model.Geofence;
import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import es.unizar.iaaa.geofencing.repository.UserRepository;
import es.unizar.iaaa.geofencing.security.model.JwtAuthenticationRequest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.jayway.restassured.RestAssured;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@ActiveProfiles("test")
public class GeofenceIntegTest {

    @Autowired
    private GeofenceRepository geofenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    private int port;

    private static final String PASSWORD = "password";

    private static final User USER1 = new User(null, "example.gmail.com", PASSWORD, "First", "Last", Date.valueOf("1992-08-07"),
            "356938035643809", new HashSet<>(), true, "ROLE_USER", Date.valueOf("2016-05-19"), new HashSet<>());

    private static final Geofence GEOFENCE1 = new Geofence(null, "Feature", null,
            new GeometryFactory().createPoint(new Coordinate(41.618618, -0.847992)), USER1, new HashSet<>());

    @Before
    public void setUp() {
        RestAssured.port = port;
        String hashedPassword = passwordEncoder.encode(PASSWORD);
        USER1.setPassword(hashedPassword);
        User currentUser = userRepository.save(USER1);
        USER1.setPassword(PASSWORD);
        currentUser.setPassword(PASSWORD);
        Map<String, String> properties = new HashMap<>();
        properties.put("name", "Prueba");
        GEOFENCE1.setProperties(properties);
        GEOFENCE1.setUser(currentUser);
    }

    @After
    public void cleanup() {
        geofenceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createGeofence() throws Exception {
        String accessToken = authenticateUser(USER1.getNick(), USER1.getPassword());

        given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(GEOFENCE1))
        .when()
                .post("/api/geofences")
        .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("id", Matchers.isA(Long.class))
                .body("type", Matchers.is(GEOFENCE1.getType()))
                .body("properties.name", Matchers.is(GEOFENCE1.getProperties().get("name")))
                .body("geometry.type", Matchers.is(GEOFENCE1.getGeometry().getGeometryType()))
                .body("geometry.coordinates", Matchers.is(GEOFENCE1.getGeometry().getCoordinates()))
                .body("user.id", Matchers.is(GEOFENCE1.getUser().getId()))
                .body("rules", Matchers.arrayWithSize(0));
    }

    private String authenticateUser(String username, String password) {
        JwtAuthenticationRequest jwt = new JwtAuthenticationRequest(username, password);
        String response =
                given()
                        .contentType(ContentType.JSON)
                        .body(jwt)
                .when()
                        .post("/api/users/auth")
                        .asString();

        JsonPath jsonPath = new JsonPath(response);
        return jsonPath.getString("token");
    }
}