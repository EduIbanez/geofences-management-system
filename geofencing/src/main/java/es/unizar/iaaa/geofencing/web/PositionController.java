package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Geometry;

import es.unizar.iaaa.geofencing.model.*;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Controller;

import java.sql.Time;
import java.util.*;

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
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionController.class);

    /**
     * This method processes the location sent by a user.
     *
     * @param map map with data
     * @return the position received
     */
    @MessageMapping("locations")
    @SendTo("/topic/positions")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Position saved", response = Position.class),
            @ApiResponse(code = 401, message = "Requires authentication", response = InsufficientAuthenticationException.class)})
    public Position savePosition(Map<String, Object> map) throws Exception {
        LOGGER.info("Requested /api/locations using WebSocket");
        String token = (String) map.get("Authorization");
        String username = jwtTokenUtil.getUsernameFromToken(token);
        if (username == null || userRepository.findByUsername(username) == null) {
            throw new InsufficientAuthenticationException("Requires authentication");
        }
        LOGGER.info("with principal "+username);
        Position position = objectMapper.readValue(objectMapper.writeValueAsString(map.get("Position")), Position.class);
        Time time = new Time(new Date().getTime());
        List<Geofence> geofences = geofenceRepository.findWithin(position.getCoordinates(), username);
        position = checkRules(position, geofences, time);
        return position;
    }

    private Position checkRules(Position position, List<Geofence> geofences, Time time) {
        Map<Geofence, GeofenceRegistry> entering = position.getEntering();
        Set<Geofence> entering_discarded = position.getEnteringDiscarded();

        Map<Geofence, GeofenceRegistry> leaving_before = position.getLeavingBefore();
        Map<Geofence, GeofenceRegistry> leaving_now = position.getLeavingNow();

        Map<Geofence, GeofenceRegistry> inside_before = position.getInsideBefore();
        Map<Geofence, GeofenceRegistry> inside_now = position.getInsideNow();
        Set<Geofence> inside_discarded = position.getInsideDiscarded();

        for (Geofence geofence: geofences) {
            Set<Rule> rules = geofence.getRules();
            for (Rule rule: rules) {
                if (rule.getEnabled()) {
                    if (rule.getType().equals(RuleType.ENTERING) && !entering.containsKey(geofence)
                            && !entering_discarded.contains(geofence)) {
                        entering.put(geofence, new GeofenceRegistry(rule.getNotifications(), time, rule.getTime()));
                    } else if (rule.getType().equals(RuleType.LEAVING)) {
                        GeofenceRegistry geofenceRegistry = new GeofenceRegistry(rule.getNotifications(), time, rule.getTime());
                        if (!leaving_before.containsKey(geofence)) {
                            leaving_before.put(geofence, geofenceRegistry);
                        }
                        leaving_now.put(geofence, geofenceRegistry);
                    } else if (rule.getType().equals(RuleType.INSIDE) && !inside_discarded.contains(geofence)) {
                        inside_now.put(geofence, new GeofenceRegistry(rule.getNotifications(), time, rule.getTime()));
                    }
                }
            }
        }

        entering_discarded.retainAll(entering.entrySet());
        Map<String, Object> map = checkEntering(entering, entering_discarded, time);

        Set<Geofence> removed_leaving = new HashSet<>(leaving_before.keySet());
        removed_leaving.removeAll(leaving_now.keySet());
        leaving_before = checkLeaving(leaving_before, removed_leaving, time);

        Set<Geofence> removed_inside = new HashSet<>(inside_before.keySet());
        removed_inside.retainAll(inside_now.keySet());
        inside_discarded = checkInside(inside_now, inside_discarded, removed_inside, time);
        inside_discarded.retainAll(inside_now.entrySet());

        return new Position(position.getCoordinates(), (Map<Geofence, GeofenceRegistry>) map.get("entering"),
                (Set<Geofence>) map.get("entering_discarded"), leaving_before, new HashMap<>(), inside_now,
                new HashMap<>(), inside_discarded);
    }

    private Map<String, Object> checkEntering(Map<Geofence, GeofenceRegistry> entering, Set<Geofence> entering_discarded,
                                        Time time) {
        Set<Geofence> geofences = new HashSet<>(entering.keySet());
        for (Geofence geofence : geofences) {
            GeofenceRegistry geofenceRegistry = entering.get(geofence);
            if (DateUtils.addSeconds(geofenceRegistry.getTime(), geofenceRegistry.getSeconds()).compareTo(time) >= 0) {
                notificationRepository.save(geofenceRegistry.getNotifications());
                entering.remove(geofence);
                entering_discarded.add(geofence);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("entering", entering);
        map.put("entering_discarded", entering_discarded);
        return map;
    }

    private Map<Geofence, GeofenceRegistry> checkLeaving(Map<Geofence, GeofenceRegistry> leaving,
                                                         Set<Geofence> removed_leaving, Time time) {
        for (Geofence geofence : removed_leaving) {
            GeofenceRegistry geofenceRegistry = leaving.get(geofence);
            if (DateUtils.addSeconds(geofenceRegistry.getTime(), geofenceRegistry.getSeconds()).compareTo(time) >= 0) {
                notificationRepository.save(geofenceRegistry.getNotifications());
                leaving.remove(geofence);
            }
        }
        return leaving;
    }

    private Set<Geofence> checkInside(Map<Geofence, GeofenceRegistry> inside, Set<Geofence> inside_discarded,
                                      Set<Geofence> removed_inside, Time time) {
        for (Geofence geofence : removed_inside) {
            GeofenceRegistry geofenceRegistry = inside.get(geofence);
            if (DateUtils.addSeconds(geofenceRegistry.getTime(), geofenceRegistry.getSeconds()).compareTo(time) >= 0) {
                notificationRepository.save(geofenceRegistry.getNotifications());
                inside_discarded.add(geofence);
            }
        }
        return inside_discarded;
    }
}