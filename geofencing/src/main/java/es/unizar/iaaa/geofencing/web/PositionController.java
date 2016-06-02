package es.unizar.iaaa.geofencing.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

import es.unizar.iaaa.geofencing.model.*;
import es.unizar.iaaa.geofencing.repository.GeofenceRegistryRepository;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import es.unizar.iaaa.geofencing.repository.NotificationRepository;
import es.unizar.iaaa.geofencing.repository.UserRepository;
import es.unizar.iaaa.geofencing.security.service.JwtTokenUtil;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
public class PositionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeofenceRepository geofenceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private GeofenceRegistryRepository geofenceRegistryRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionController.class);

    /**
     * This method processes the location sent by a user.
     *
     * @param positionAuthenticated token and position
     * @return the position received
     */
    @MessageMapping("locations")
    @SendTo("/topic/positions")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Position saved", response = Position.class),
            @ApiResponse(code = 401, message = "Requires authentication", response = InsufficientAuthenticationException.class)})
    public Position savePosition(PositionAuthenticated positionAuthenticated) throws Exception {
        LOGGER.info("Requested /api/locations using WebSocket");
        String token = positionAuthenticated.getAuthorization();
        String username = jwtTokenUtil.getUsernameFromToken(token);
        if (username == null || userRepository.findByUsername(username) == null) {
            throw new InsufficientAuthenticationException("Requires authentication");
        }
        LOGGER.info("with principal "+username);
        Calendar time = Calendar.getInstance();
        Position position = positionAuthenticated.getPosition();
        List<Geofence> geofences = geofenceRepository.findWithin(position.getCoordinates(), username);
        User user = userRepository.findByUsername(username);
        checkRules(geofences, user, time);
        return position;
    }

    public void checkRules(List<Geofence> geofences, User user, Calendar calendar) {
        GeofenceRegistry previousGeofenceRegistry = geofenceRegistryRepository.findFirstByEmailOrderByDateDesc(user.getEmail());
        Map<Long, Date> entering = new HashMap<>();
        if (previousGeofenceRegistry != null) {
            entering.putAll(previousGeofenceRegistry.getEntering());
            for (Long geofence_id : entering.keySet()) {
                Geofence geofence = geofenceRepository.findOne(geofence_id);
                for (Rule rule : geofence.getRules()) {
                    if (rule.getType().equals(RuleType.ENTERING)) {
                        entering = checkEntering(user, rule, calendar, previousGeofenceRegistry.getEntering(), entering, geofence_id);
                    }
                }
            }
        }
        Map<Long, Date> leaving = new HashMap<>();
        if (previousGeofenceRegistry != null) {
            leaving.putAll(previousGeofenceRegistry.getLeaving());
        }
        Map<Long, Date> inside = new HashMap<>();
        for (Geofence geofence : geofences) {
            for (Rule rule: geofence.getRules()) {
                if (rule.getEnabled()) {
                    if (rule.getType().equals(RuleType.ENTERING)) {
                        if (previousGeofenceRegistry == null || !previousGeofenceRegistry.getEntering().containsKey(geofence.getId())) {
                            entering = checkEntering(user, rule, calendar, null, entering, geofence.getId());
                        }
                    } else if (rule.getType().equals(RuleType.LEAVING)) {
                        if (previousGeofenceRegistry == null || !previousGeofenceRegistry.getLeaving().containsKey(geofence.getId())) {
                            leaving.put(geofence.getId(), calendar.getTime());
                        }
                    } else if (rule.getType().equals(RuleType.INSIDE)) {
                        if (previousGeofenceRegistry != null && previousGeofenceRegistry.getInside().containsKey(geofence.getId())) {
                            inside = checkInside(user, rule, calendar, previousGeofenceRegistry.getInside(), inside, geofence.getId());
                        } else {
                            inside = checkInside(user, rule, calendar, null, inside, geofence.getId());
                        }
                    }
                }
            }
        }
        List<Long> geofences_id = geofences.stream().map(Geofence::getId).collect(Collectors.toList());
        for (Map.Entry<Long, Date> entry : entering.entrySet()) {
            if (!geofences_id.contains(entry.getKey()) && entry.getValue().compareTo(new Date(0)) == 0) {
                entering.remove(entry.getKey());
            }
        }
        for (Map.Entry<Long, Date> entry : leaving.entrySet()) {
            if (!geofences_id.contains(entry.getKey())) {
                Geofence geofence = geofenceRepository.findOne(entry.getKey());
                for (Rule rule : geofence.getRules()) {
                    leaving = checkLeaving(user, rule, calendar, leaving, geofence.getId());
                }
            }
        }
        geofenceRegistryRepository.save(
                new GeofenceRegistry(null, entering, leaving, inside, user.getEmail(), calendar.getTime()));
    }

    private Map<Long, Date> checkEntering(User user, Rule rule, Calendar calendar, Map<Long, Date> previous,
                                          Map<Long, Date> entering, Long geofence_id) {
        Calendar aux = checkTime(user, rule, calendar, previous, geofence_id);
        if (aux != null) {
            entering.replace(geofence_id, aux.getTime());
        } else {
            entering.put(geofence_id, calendar.getTime());
        }
        return entering;
    }

    private Map<Long, Date> checkLeaving(User user, Rule rule, Calendar calendar, Map<Long, Date> leaving, Long geofence_id) {
        Calendar aux = Calendar.getInstance();
        aux.setTime(leaving.get(geofence_id));
        aux.add(Calendar.SECOND, rule.getTime());
        if (calendar.getTime().compareTo(aux.getTime()) >= 0) {
            notificationRepository.save(new Notification(null, rule, user, "No leído", new java.sql.Date(calendar.getTime().getTime())));
            leaving.remove(geofence_id);
        } else {
            aux.add(Calendar.SECOND, -(rule.getTime()));
        }
        return leaving;

    }

    private Map<Long, Date> checkInside(User user, Rule rule, Calendar calendar, Map<Long, Date> previous,
                                          Map<Long, Date> inside, Long geofence_id) {
        Calendar aux = checkTime(user, rule, calendar, previous, geofence_id);
        if (aux != null) {
            inside.put(geofence_id, aux.getTime());
        } else {
            inside.put(geofence_id, calendar.getTime());
        }
        return inside;
    }

    public Calendar checkTime(User user, Rule rule, Calendar calendar, Map<Long, Date> previous, Long geofence_id) {
        Calendar aux = null;
        if (previous != null) {
            aux = Calendar.getInstance();
            aux.setTime(previous.get(geofence_id));
            aux.add(Calendar.SECOND, rule.getTime());
            if (calendar.getTime().compareTo(aux.getTime()) >= 0 && (aux.getTime().getTime()/1000) != rule.getTime()) {
                notificationRepository.save(new Notification(null, rule, user, "No leído", new java.sql.Date(calendar.getTime().getTime())));
                aux.setTime(new Date(0));
            } else {
                aux.add(Calendar.SECOND, -(rule.getTime()));
            }
        } else {
            if (rule.getTime() == 0) {
                notificationRepository.save(new Notification(null, rule, user, "No leído", new java.sql.Date(calendar.getTime().getTime())));
                aux = Calendar.getInstance();
                aux.setTime(new Date(0));
            }
        }
        return aux;
    }
}