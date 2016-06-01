package es.unizar.iaaa.geofencing.config;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.HashSet;

import es.unizar.iaaa.geofencing.builder.GeometryBuilder;
import es.unizar.iaaa.geofencing.model.Geofence;
import es.unizar.iaaa.geofencing.model.Notification;
import es.unizar.iaaa.geofencing.model.Rule;
import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import es.unizar.iaaa.geofencing.repository.NotificationRepository;
import es.unizar.iaaa.geofencing.repository.RuleRepository;
import es.unizar.iaaa.geofencing.repository.UserRepository;

import static es.unizar.iaaa.geofencing.model.RuleType.ENTERING;
import static es.unizar.iaaa.geofencing.model.RuleType.INSIDE;

@Component
@Profile("!test")
public class DatabaseFillerOnStartup implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeofenceRepository geofenceRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String PASSWORD = "admin";

    private User ADMIN = new User(null, "admin", null, "First", "Last", Date.valueOf("1992-08-07"),
            "", new HashSet<>(), true, "ROLE_ADMIN", Date.valueOf("2016-05-19"), new HashSet<>());

    private Coordinate[] coordinates = {new Coordinate(41.63266, -0.898361), new Coordinate(41.61962, -0.8025762),
            new Coordinate(41.613191, -0.873092), new Coordinate(41.63266, -0.898361)};
    private Geofence GEOFENCE = new Geofence(null, "Feature", null,
            new GeometryFactory().createPolygon(coordinates), ADMIN, new HashSet<>());

    private Coordinate[] coordinates2 = {new Coordinate(41.65, -0.883333), new Coordinate(41.634742, -0.899631),
            new Coordinate(41.625359, -0.859376), new Coordinate(41.65, -0.883333)};
    private Geofence GEOFENCE2 = new Geofence(null, "Feature", null,
            new GeometryFactory().createPolygon(coordinates2), ADMIN, new HashSet<>());

    private Geofence GEOFENCE3 = new Geofence(null, "Feature", null,
            new GeometryBuilder().circle(41.682746, -0.888445, 0.002, 40), ADMIN, new HashSet<>());

    private Rule RULE = new Rule(null, true, ENTERING, 70, "You are inside", new HashSet<>(),
            new HashSet<>(), GEOFENCE);

    private Rule RULE2 = new Rule(null, true, ENTERING, 20, "You are outside", new HashSet<>(),
            new HashSet<>(), GEOFENCE2);

    private Rule RULE3 = new Rule(null, true, INSIDE, 10, "You are inside", new HashSet<>(),
            new HashSet<>(), GEOFENCE);

    private Notification NOTIFICATION = new Notification(null, RULE, ADMIN, "No leído", Date.valueOf("2016-01-19"));

    private Notification NOTIFICATION2 = new Notification(null, RULE2, ADMIN, "No leído", Date.valueOf("2016-01-20"));

    private Notification NOTIFICATION3 = new Notification(null, RULE3, ADMIN, "No leído", Date.valueOf("2016-01-18"));

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userRepository.findByUsername("admin") == null) {
            ADMIN.setPassword(passwordEncoder.encode(PASSWORD));
            userRepository.save(ADMIN);
            geofenceRepository.save(GEOFENCE);
            geofenceRepository.save(GEOFENCE2);
            geofenceRepository.save(GEOFENCE3);
            ruleRepository.save(RULE);
            ruleRepository.save(RULE2);
            ruleRepository.save(RULE3);
            notificationRepository.save(NOTIFICATION);
            notificationRepository.save(NOTIFICATION2);
            notificationRepository.save(NOTIFICATION3);
        }
    }
}