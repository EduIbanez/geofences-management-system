package es.unizar.iaaa.geofencing.web;

import com.vividsolutions.jts.geom.Geometry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.sql.Time;
import java.util.Date;

import es.unizar.iaaa.geofencing.model.Position;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionController.class);

    /**
     * This method processes the location sent by a user.
     *
     * @param location data of the location
     * @return the position received
     */
    @MessageMapping("locations")
    @SendTo("/topic/positions")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Position saved", response = Position.class)})
    public Position savePosition(Geometry location) throws Exception {
        LOGGER.info("Requested /api/locations using WebSocket: "+location.toText());
        Time time = new Time(new Date().getTime());
        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //UserDetails customUser = (UserDetails) auth.getPrincipal();
        //String email = customUser.getUsername();
        //User user = userRepository.findByUsername(email);

        Position positionSaved = positionRepository.save(new Position(null, location, time, null));
        return positionSaved;
    }
}