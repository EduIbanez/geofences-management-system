package es.unizar.iaaa.geofencing.web;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import es.unizar.iaaa.geofencing.Application;
import es.unizar.iaaa.geofencing.model.Geofence;
import es.unizar.iaaa.geofencing.model.GeofenceRegistry;
import es.unizar.iaaa.geofencing.model.Notification;
import es.unizar.iaaa.geofencing.model.Position;
import es.unizar.iaaa.geofencing.model.Rule;
import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.GeofenceRegistryRepository;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import es.unizar.iaaa.geofencing.repository.NotificationRepository;
import es.unizar.iaaa.geofencing.repository.RuleRepository;
import es.unizar.iaaa.geofencing.repository.UserRepository;

import static es.unizar.iaaa.geofencing.model.RuleType.ENTERING;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes={Application.class})
@ActiveProfiles("test")
public class CheckRulesTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeofenceRepository geofenceRepository;

    @Autowired
    private GeofenceRegistryRepository geofenceRegistryRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PositionController positionController;

    private User USER1;

    @Before
    public void setup() {
        String PASSWORD = "password";
        USER1 = new User(null, "example.gmail.com", PASSWORD, "First", "Last", java.sql.Date.valueOf("1992-08-07"),
                "356938035643809", new HashSet<>(), true, "ROLE_USER", java.sql.Date.valueOf("2016-05-19"), new HashSet<>());

        String hashedPassword = passwordEncoder.encode(PASSWORD);
        USER1.setPassword(hashedPassword);
        userRepository.save(USER1);
        USER1.setPassword(PASSWORD);

        Coordinate[] coordinates = {new Coordinate(41.63266, -0.898361), new Coordinate(41.61962, -0.8025762),
                new Coordinate(41.613191, -0.873092), new Coordinate(41.63266, -0.898361)};
        Geofence GEOFENCE1 = new Geofence(null, "Feature", null,
                new GeometryFactory().createPolygon(coordinates), USER1, new HashSet<>());
        GEOFENCE1 = geofenceRepository.save(GEOFENCE1);

        Rule RULE1 = new Rule(null, true, ENTERING, 10, "You are inside", new HashSet<>(), new HashSet<>(), GEOFENCE1);
        ruleRepository.save(RULE1);
    }

    @After
    public void cleanup() {
        geofenceRegistryRepository.deleteAll();
        notificationRepository.deleteAll();
        ruleRepository.deleteAll();
        geofenceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void checkRules() throws Exception {
        GeofenceRegistry previousGeofenceRegistry = geofenceRegistryRepository.findFirstByNickOrderByDateDesc(USER1.getNick());
        Calendar time = Calendar.getInstance();
        Position position = new Position(new GeometryFactory().createPoint(new Coordinate(41.618618, -0.847992)));
        List<Geofence> geofences = geofenceRepository.findWithin(position.getCoordinates(), USER1.getNick());
        Map<Long, Date> entering = new HashMap<>();
        Map<Long, Date> leaving = new HashMap<>();
        Map<Long, Date> inside = new HashMap<>();
        List<Notification> notifications = positionController.checkRules(geofences, USER1, time, entering,
                leaving, inside, previousGeofenceRegistry);
        geofenceRegistryRepository.save(new GeofenceRegistry(null, entering, leaving, inside, USER1.getNick(), time.getTime()));
        assertEquals(1, entering.size());
        assertEquals(0, leaving.size());
        assertEquals(0, inside.size());
        assertEquals(0, notifications.size());

        previousGeofenceRegistry = geofenceRegistryRepository.findFirstByNickOrderByDateDesc(USER1.getNick());
        time.add(Calendar.SECOND, 30);
        geofences = geofenceRepository.findWithin(position.getCoordinates(), USER1.getNick());
        entering = previousGeofenceRegistry.getEntering();
        leaving = previousGeofenceRegistry.getLeaving();
        inside = previousGeofenceRegistry.getInside();
        notifications = positionController.checkRules(geofences, USER1, time, entering, leaving, inside, previousGeofenceRegistry);
        assertEquals(1, entering.size());
        assertEquals(0, leaving.size());
        assertEquals(0, inside.size());
        assertEquals(1, notifications.size());
    }

}
