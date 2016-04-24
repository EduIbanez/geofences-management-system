package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import es.unizar.iaaa.geofencing.Application;
import es.unizar.iaaa.geofencing.domain.Geofence;
import es.unizar.iaaa.geofencing.domain.Rule;
import es.unizar.iaaa.geofencing.domain.User;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import es.unizar.iaaa.geofencing.repository.RuleRepository;
import es.unizar.iaaa.geofencing.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class RuleControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private GeofenceRepository geofenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private static final User USER1 = new User(null, "example.gmail.com", "password", "First",
            "Last", "07/08/1992", "356938035643809", new HashSet<>(), true, "user", new HashSet<>());

    private static final Geofence GEOFENCE1 = new Geofence(null, "Feature", null,
            new GeometryFactory().createPoint(new Coordinate(1, 2)), USER1, new HashSet<>());

    private static final Rule RULE1 = new Rule(null, true, "INSIDE", 10, "You are inside", new HashSet<>(),
            new HashSet<>(), GEOFENCE1);

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        User currentUser = userRepository.save(USER1);
        Geofence currentGeofence = geofenceRepository.save(GEOFENCE1);
    }

    @After
    public void cleanup() {
        ruleRepository.deleteAll();
        geofenceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createRule() throws Exception {
        this.mockMvc.perform(post("/api/rules")
                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(objectMapper.writeValueAsString(RULE1))
                .with(httpBasic(USER1.getEmail(), USER1.getPassword())))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.enabled").value(RULE1.getEnabled()))
                .andExpect(jsonPath("$.type").value(RULE1.getType()))
                .andExpect(jsonPath("$.time").value(RULE1.getTime()))
                .andExpect(jsonPath("$.message").value(RULE1.getMessage()))
                .andExpect(jsonPath("$.days").isEmpty())
                .andExpect(jsonPath("$.notifications").isEmpty())
                .andExpect(jsonPath("$.geofence.id").value(RULE1.getGeofence().getId().intValue()));
        assertEquals(1, ruleRepository.count());
    }
}