package es.unizar.iaaa.geofencing.web;

import es.unizar.iaaa.geofencing.domain.Position;
import es.unizar.iaaa.geofencing.repository.PositionRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@Configuration
@EnableWebSocketMessageBroker
public class PositionController {

    @Autowired
    private PositionRepository positionRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionController.class);

    /**
     * This method processes the position sent by a user.
     *
     * @param position data of the position
     * @return the position received
     */
    @MessageMapping("/positions")
    @SendTo("/topic/positions")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Position saved", response = Position.class)})
    public Position savePosition(Position position) throws Exception {
        LOGGER.info("Requested /api/positions using WebSocket");
        position.setId(null);
        Position positionSaved = positionRepository.save(position);
        return positionSaved;
    }
}