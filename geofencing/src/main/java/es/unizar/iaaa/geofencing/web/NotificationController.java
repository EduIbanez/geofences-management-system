package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.annotation.JsonView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import es.unizar.iaaa.geofencing.model.Notification;
import es.unizar.iaaa.geofencing.repository.NotificationRepository;
import es.unizar.iaaa.geofencing.view.View;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    /**
     * This method creates a new notification.
     *
     * @param notification data of the notification
     * @return the notification created
     */
    @RequestMapping(path = "/api/notifications", method = RequestMethod.POST)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Notification created",
                    responseHeaders = @ResponseHeader(name = "Location", description = "Location",
                            response = URI.class), response = Notification.class)})
    public ResponseEntity<Notification> createNotification(@RequestBody final Notification notification) {
        LOGGER.info("Requested /api/notifications POST method");
        notification.setId(null);
        Notification notificationCreated = notificationRepository.save(notification);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(notification.getId()).toUri());
        return new ResponseEntity<>(notificationCreated, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * This method returns an array of notifications of an authenticated user. If there is nobody authenticated, it
     * returns an empty array.
     *
     * @return an array of notifications
     */
    @RequestMapping(path = "/api/notifications", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Array of notifications", response = String.class),
            @ApiResponse(code = 401, message = "Requires authentication", response = InsufficientAuthenticationException.class),
            @ApiResponse(code = 500, message = "Error parsing into JSON", response = NotificationMappingJsonException.class)})
    public String getNotifications() {
        LOGGER.info("Requested /api/notifications GET method");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info("Requested /api/notifications GET method for "+auth.getPrincipal());
        if (!(auth.getPrincipal() instanceof UserDetails)) {
            throw new InsufficientAuthenticationException("Requires authentication");
        }
        UserDetails customUser = (UserDetails) auth.getPrincipal();
        String email = customUser.getUsername();
        List<Notification> notifications = notificationRepository.find(email);
        try {
            return objectMapper.writerWithView(View.NotificationCompleteView.class)
                    .writeValueAsString(notifications);
        } catch (JsonProcessingException e) {
            throw new NotificationMappingJsonException();
        }
    }

    /**
     * This method modifies the data of a previously created notification.
     *
     * @param id            unique identifier representing a specific rule
     * @param notification  data of the notification
     * @return the notification modified
     */
    @RequestMapping(path = "/api/notifications/{id}", method = RequestMethod.PUT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notification modified", response = Notification.class),
            @ApiResponse(code = 409, message = "Notification state doesn't permit request", response = NotificationResourceConflictException.class),
            @ApiResponse(code = 404, message = "Notification not found", response = NotificationNotFoundException.class)})
    @JsonView(View.NotificationCompleteView.class)
    public Notification modifyNotification(@PathVariable("id") Long id, @RequestBody Notification notification) {
        LOGGER.info("Requested /api/notifications/{id} PUT method");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails customUser = (UserDetails) auth.getPrincipal();
        String email = customUser.getUsername();
        if (!notificationRepository.existsByUsername(id, email)) {
            throw new NotificationNotFoundException();
        }
        notification.setId(id);
        try {
            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw new NotificationResourceConflictException(id, e);
        }
    }

    /**
     * This method deletes the data of a previously created notification.
     *
     * @param id unique identifier representing a specific notification
     * @return the notification deleted
     */
    @RequestMapping(path = "/api/notifications/{id}", method = RequestMethod.DELETE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notification deleted", response = Notification.class),
            @ApiResponse(code = 404, message = "Notification not found", response = NotificationNotFoundException.class)})
    public Notification deleteNotification(@PathVariable("id") Long id) {
        LOGGER.info("Requested /api/notifications/{id} DELETE method");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails customUser = (UserDetails) auth.getPrincipal();
        String email = customUser.getUsername();
        if (notificationRepository.existsByUsername(id, email)) {
            notificationRepository.delete(id);
            return null;
        } else {
            throw new NotificationNotFoundException();
        }
    }

    /**
     * This method returns a notification by id.
     *
     * @param id unique identifier representing a specific notification
     * @return the notification requested
     */
    @RequestMapping(path = "/api/notifications/{id}", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Notification requested", response = String.class),
            @ApiResponse(code = 404, message = "Notification not found", response = NotificationNotFoundException.class),
            @ApiResponse(code = 500, message = "Error parsing into JSON", response = NotificationMappingJsonException.class)})
    public String getNotification(@PathVariable("id") Long id) {
        LOGGER.info("Requested /api/notifications/{id} GET method");
        if (notificationRepository.exists(id)) {
            Notification notification = notificationRepository.findOne(id);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            try {
                if (auth instanceof AnonymousAuthenticationToken) {
                        return objectMapper.writerWithView(View.NotificationBaseView.class)
                                .writeValueAsString(notification);
                } else {
                    return objectMapper.writerWithView(View.NotificationCompleteView.class)
                            .writeValueAsString(notification);
                }
            } catch (JsonProcessingException e) {
                throw new NotificationMappingJsonException();
            }
        } else {
            throw new NotificationNotFoundException();
        }
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Notification status forbids request")
    public class NotificationResourceConflictException extends RuntimeException {
        public NotificationResourceConflictException(Long id, Exception e) {
            super("Failed update of Notification "+id, e);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Notification")
    public class NotificationNotFoundException extends RuntimeException { }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error mapping Notifications into JSON")
    public class NotificationMappingJsonException extends RuntimeException { }
}