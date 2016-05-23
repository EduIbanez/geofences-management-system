package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Geometry;

import es.unizar.iaaa.geofencing.security.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Controller;

import java.sql.Time;
import java.util.Date;
import java.util.Map;

import es.unizar.iaaa.geofencing.model.Position;
import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.PositionRepository;
import es.unizar.iaaa.geofencing.repository.UserRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
public class PositionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PositionRepository positionRepository;

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
        Geometry location = objectMapper.readValue(objectMapper.writeValueAsString(map.get("Geometry")), Geometry.class);
        Time time = new Time(new Date().getTime());
        User user = userRepository.findByUsername(username);
        Position positionSaved = positionRepository.save(new Position(null, location, time, user));
        return positionSaved;
    }
}