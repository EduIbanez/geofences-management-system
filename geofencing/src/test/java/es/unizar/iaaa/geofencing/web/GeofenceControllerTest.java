package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;

import es.unizar.iaaa.geofencing.Application;
import es.unizar.iaaa.geofencing.model.Geofence;
import es.unizar.iaaa.geofencing.model.Properties;
import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import es.unizar.iaaa.geofencing.repository.UserRepository;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class GeofenceControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private GeofenceRepository geofenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private static final User USER1 = new User(null, "example.gmail.com", "password", "First",
            "Last", "07/08/1992", "356938035643809", new HashSet<>());

    private static final Geofence GEOFENCE1 = new Geofence(null, "Feature", new Properties("Prueba"),
            new GeometryFactory().createPoint(new Coordinate(1, 2)), USER1);

    private static final Logger LOGGER = LoggerFactory.getLogger(GeofenceControllerTest.class);

    private User currentUser;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        currentUser = userRepository.save(USER1);
        GEOFENCE1.setUser(currentUser);
    }

    @After
    public void cleanup() {
        geofenceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createGeofence() throws Exception {
        mockMvc.perform(post("/api/geofences")
                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(objectMapper.writeValueAsString(GEOFENCE1)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.type").value(GEOFENCE1.getType()))
                .andExpect(jsonPath("$.properties.name").value(GEOFENCE1.getProperties().getName()))
                .andExpect(jsonPath("$.geometry.type").value(GEOFENCE1.getGeometry().getGeometryType()))
                .andExpect(jsonPath("$.user.id").value(GEOFENCE1.getUser().getId().intValue()));
        assertEquals(1, geofenceRepository.count());
    }


}