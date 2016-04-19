package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import es.unizar.iaaa.geofencing.Application;
import es.unizar.iaaa.geofencing.domain.Geofence;
import es.unizar.iaaa.geofencing.domain.User;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import es.unizar.iaaa.geofencing.repository.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
            "Last", "07/08/1992", "356938035643809", new HashSet<>(), true, "user");

    private static final Geofence GEOFENCE1 = new Geofence(null, "Feature", null,
            new GeometryFactory().createPoint(new Coordinate(1, 2)), USER1);

    private final int[] COORDINATES = {1, 2};
    private final int COUNT = 10;
    private final int RADIUS = 3;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        User currentUser = userRepository.save(USER1);
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

    // TODO Autenticación
    @Test
    public void createGeofence() throws Exception {
        this.mockMvc.perform(post("/api/geofences")
                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(objectMapper.writeValueAsString(GEOFENCE1)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.type").value(GEOFENCE1.getType()))
                .andExpect(jsonPath("$.properties.name").value(GEOFENCE1.getProperties().get("name")))
                .andExpect(jsonPath("$.geometry.type").value(GEOFENCE1.getGeometry().getGeometryType()))
                .andExpect(jsonPath("$.user.id").value(GEOFENCE1.getUser().getId().intValue()));
        assertEquals(1, geofenceRepository.count());
    }

    // TODO Dos vistas: con autenticación y sin autenticación
    @Test
    public void getGeofencesWithLimit() throws Exception {
        Geofence auxGeofence = GEOFENCE1;
        for (int i = 0; i < COUNT; i++) {
            auxGeofence.setId(null);
            auxGeofence.setGeometry(new GeometryFactory().createPoint(new Coordinate(COORDINATES[0]+i, COORDINATES[1]+i)));
            auxGeofence = geofenceRepository.save(auxGeofence);
        }
        int LIMIT = 2;
        this.mockMvc.perform(get("/api/geofences")
                .param("limit", String.valueOf(LIMIT))
                .param("latitude", String.valueOf(COORDINATES[0]))
                .param("longitude", String.valueOf(COORDINATES[1]))
                .param("radius", String.valueOf(RADIUS)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(LIMIT)));
        assertEquals(COUNT, geofenceRepository.count());
    }

    // TODO Dos vistas: con autenticación y sin autenticación
    @Test
    public void getGeofencesWithoutLimit() throws Exception {
        Geofence auxGeofence = GEOFENCE1;
        for (int i = 0; i < COUNT; i++) {
            auxGeofence.setId(null);
            auxGeofence.setGeometry(new GeometryFactory().createPoint(new Coordinate(COORDINATES[0]+i, COORDINATES[1]+i)));
            auxGeofence = geofenceRepository.save(auxGeofence);
        }
        this.mockMvc.perform(get("/api/geofences")
                .param("latitude", String.valueOf(COORDINATES[0]))
                .param("longitude", String.valueOf(COORDINATES[1]))
                .param("radius", String.valueOf(RADIUS)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(RADIUS)));
        assertEquals(COUNT, geofenceRepository.count());
    }

    // TODO Con autenticación
    @Test
    @Transactional
    public void modifyGeofence() throws Exception {
        Geofence geofence = geofenceRepository.save(GEOFENCE1);
        geofence.getProperties().put("name", "Proof");
        this.mockMvc.perform(put("/api/geofences/"+geofence.getId())
                .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                .content(objectMapper.writeValueAsString(geofence)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(geofence.getId().intValue()))
                .andExpect(jsonPath("$.type").value(geofence.getType()))
                .andExpect(jsonPath("$.properties.name").value(geofence.getProperties().get("name")))
                .andExpect(jsonPath("$.geometry.type").value(geofence.getGeometry().getGeometryType()))
                .andExpect(jsonPath("$.user.id").value(geofence.getUser().getId().intValue()));
        Geofence geofenceNew = geofenceRepository.findOne(geofence.getId());
        assertEquals(geofence.getProperties().get("name"), geofenceNew.getProperties().get("name"));
    }

    // TODO Con autenticación
    @Test
    public void deleteGeofence() throws Exception {
        Geofence geofence = geofenceRepository.save(GEOFENCE1);
        this.mockMvc.perform(delete("/api/geofences/"+geofence.getId()))
                .andExpect(status().isOk());
        assertNull(userRepository.findOne(geofence.getId()));
    }

    // TODO Dos vistas: con autenticación y sin autenticación
    @Test
    public void getGeofence() throws Exception {
        Geofence geofence = geofenceRepository.save(GEOFENCE1);
        this.mockMvc.perform(get("/api/geofences/"+geofence.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json; charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(geofence.getId().intValue()))
                .andExpect(jsonPath("$.type").value(geofence.getType()))
                .andExpect(jsonPath("$.properties.name").value(geofence.getProperties().get("name")))
                .andExpect(jsonPath("$.geometry.type").value(geofence.getGeometry().getGeometryType()))
                .andExpect(jsonPath("$.user.id").value(geofence.getUser().getId().intValue()));
    }
}