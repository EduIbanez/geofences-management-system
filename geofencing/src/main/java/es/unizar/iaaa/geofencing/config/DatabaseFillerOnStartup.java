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


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userRepository.findByUsername("admin") == null) {
            populate();
        }
    }

    public void populate() {
        String PASSWORD = "admin";

        User adminUser = new User(null, "admin", null, "First", "Last", Date.valueOf("1992-08-07"),
                "", new HashSet<>(), true, "ROLE_ADMIN", Date.valueOf("2016-05-19"), new HashSet<>());

        Coordinate[] coordinates = {new Coordinate(41.63266, -0.898361), new Coordinate(41.61962, -0.8025762),
                new Coordinate(41.613191, -0.873092), new Coordinate(41.63266, -0.898361)};

        Geofence geofence = new Geofence(null, "Feature", null,
                new GeometryFactory().createPolygon(coordinates), null, new HashSet<>());

        Coordinate[] coordinates2 = {new Coordinate(41.65, -0.883333), new Coordinate(41.634742, -0.899631),
                new Coordinate(41.625359, -0.859376), new Coordinate(41.65, -0.883333)};

        Geofence geofence2 = new Geofence(null, "Feature", null,
                new GeometryFactory().createPolygon(coordinates2), null, new HashSet<>());

        Geofence geofence3 = new Geofence(null, "Feature", null,
                new GeometryBuilder().circle(41.682746, -0.888445, 0.002, 40), null, new HashSet<>());

        Rule rule = new Rule(null, true, ENTERING, 70, "You are entering", new HashSet<>(),
                new HashSet<>(), geofence);

        Rule rule2 = new Rule(null, true, ENTERING, 20, "You are outside", new HashSet<>(),
                new HashSet<>(), geofence2);

        Rule rule3 = new Rule(null, true, INSIDE, 10, "You are inside", new HashSet<>(),
                new HashSet<>(), geofence);

        Notification notification = new Notification(null, null, null, "No leído", Date.valueOf("2016-01-19"));

        Notification notification2 = new Notification(null, null, null, "No leído", Date.valueOf("2016-01-20"));

        Notification notification3 = new Notification(null, null, null, "No leído", Date.valueOf("2016-01-18"));

        adminUser.setPassword(passwordEncoder.encode(PASSWORD));
        adminUser = userRepository.save(adminUser);

        geofence.setUser(adminUser);
        adminUser.getGeofences().add(geofence);
        geofence = geofenceRepository.save(geofence);


        geofence2.setUser(adminUser);
        adminUser.getGeofences().add(geofence2);
        geofence2 = geofenceRepository.save(geofence2);

        geofence3.setUser(adminUser);
        adminUser.getGeofences().add(geofence3);
        geofence3 = geofenceRepository.save(geofence3);

        rule.setGeofence(geofence);
        geofence.getRules().add(rule);
        rule = ruleRepository.save(rule);

        rule2.setGeofence(geofence);
        geofence.getRules().add(rule2);
        ruleRepository.save(rule2);

        rule3.setGeofence(geofence);
        geofence.getRules().add(rule3);
        ruleRepository.save(rule3);

        notification.setRule(rule);
        rule.getNotifications().add(notification);
        notificationRepository.save(notification);

        notification2.setRule(rule);
        rule.getNotifications().add(notification2);
        notificationRepository.save(notification2);

        notification3.setRule(rule);
        rule.getNotifications().add(notification3);
        notificationRepository.save(notification3);
    }

    public void cleanup() {
        notificationRepository.deleteAll();
        ruleRepository.deleteAll();
        geofenceRepository.deleteAll();
        userRepository.deleteAll();
    }
}