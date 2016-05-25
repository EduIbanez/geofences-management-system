package es.unizar.iaaa.geofencing.config;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import es.unizar.iaaa.geofencing.model.Geofence;
import es.unizar.iaaa.geofencing.model.Notification;
import es.unizar.iaaa.geofencing.model.Rule;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import es.unizar.iaaa.geofencing.repository.NotificationRepository;
import es.unizar.iaaa.geofencing.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.HashSet;

import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.UserRepository;

import static es.unizar.iaaa.geofencing.model.RuleType.INSIDE;

@Component
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
            "", new HashSet<>(), true, "ROLE_ADMIN", Date.valueOf("2016-05-19"), new HashSet<>(), new HashSet<>());

    private Geofence GEOFENCE = new Geofence(null, "Feature", null,
            new GeometryFactory().createPoint(new Coordinate(1, 2)), ADMIN, new HashSet<>());

    private Coordinate[] coordinates = {new Coordinate(41.65, -0.883333), new Coordinate(41.634742, -0.899631),
            new Coordinate(41.625359, -0.859376), new Coordinate(41.65, -0.883333)};
    private Geofence GEOFENCE2 = new Geofence(null, "Feature", null,
            new GeometryFactory().createPolygon(coordinates), ADMIN, new HashSet<>());

    private Rule RULE = new Rule(null, true, INSIDE, 10, "You are inside", new HashSet<>(),
            new HashSet<>(), GEOFENCE2);

    private Notification NOTIFICATION = new Notification(null, RULE, ADMIN, "No leído", Date.valueOf("2016-01-19"));

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userRepository.findByUsername("admin") == null) {
            ADMIN.setPassword(passwordEncoder.encode(PASSWORD));
            userRepository.save(ADMIN);
            geofenceRepository.save(GEOFENCE);
            geofenceRepository.save(GEOFENCE2);
            ruleRepository.save(RULE);
            notificationRepository.save(NOTIFICATION);
        }
    }
}