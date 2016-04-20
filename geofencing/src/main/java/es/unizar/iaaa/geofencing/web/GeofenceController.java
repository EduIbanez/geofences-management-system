package es.unizar.iaaa.geofencing.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import es.unizar.iaaa.geofencing.domain.Geofence;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import es.unizar.iaaa.geofencing.view.View;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
public class GeofenceController {

    @Autowired
    private GeofenceRepository geofenceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(GeofenceController.class);

    /**
     * This method creates a new geofence.
     *
     * @param geofence data of the geofence
     * @return the geofence created
     */
    @RequestMapping(path = "/api/geofences", method = RequestMethod.POST)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Geofence created",
                    responseHeaders = @ResponseHeader(name = "Location", description = "Location",
                            response = URI.class), response = Geofence.class)})
    public ResponseEntity<Geofence> createGeofence(@RequestBody final Geofence geofence) {
        LOGGER.info("Requested /api/geofences POST method");
        geofence.setId(null);
        Geofence geofenceCreated = geofenceRepository.save(geofence);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(geofence.getId()).toUri());
        return new ResponseEntity<>(geofenceCreated, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * This method returns an array of those geofences that are inside of an area generated by an specific point
     * and a radius.
     *
     * @param limit     maximum of geofences to be returned
     * @param latitude  latitude of a specific point
     * @param longitude longitude of a specific point
     * @param radius    distance to determine the area of a circle, taking the specific point as the center of this circle
     * @return an array of geofences
     */
    @RequestMapping(path = "/api/geofences", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Array of geofences", response = List.class)})
    public MappingJacksonValue getGeofences(@RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "latitude") Double latitude,
                                            @RequestParam(value = "longitude") Double longitude,
                                            @RequestParam(value = "radius") Integer radius) {
        LOGGER.info("Requested /api/geofences GET method");
        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        shapeFactory.setNumPoints(32);
        shapeFactory.setCentre(new Coordinate(latitude, longitude));
        shapeFactory.setSize(radius * 2);
        List<Geofence> geofences;
        if (limit != null) {
            geofences = geofenceRepository.findWithin(shapeFactory.createCircle(), new PageRequest(0, limit));
        }
        else {
            geofences = geofenceRepository.findWithin(shapeFactory.createCircle());
        }
        final MappingJacksonValue result = new MappingJacksonValue(geofences);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((auth instanceof AnonymousAuthenticationToken)) {
            result.setSerializationView(View.GeofenceBaseView.class);
        } else {
            result.setSerializationView(View.GeofenceCompleteView.class);
        }
        return result;
    }

    /**
     * This method modifies the data of a previously created geofence.
     *
     * @param id       unique identifier representing a specific geofence
     * @param geofence data of the geofence
     * @return the geofence modified
     */
    @RequestMapping(path = "/api/geofences/{id}", method = RequestMethod.PUT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Geofence modified", response = Geofence.class),
            @ApiResponse(code = 409, message = "Geofence state doesn't permit request", response = GeofenceResourceConflictException.class),
            @ApiResponse(code = 404, message = "Geofence not found", response = GeofenceNotFoundException.class)})
    @JsonView(View.GeofenceCompleteView.class)
    public Geofence modifyGeofence(@PathVariable("id") Long id, @RequestBody Geofence geofence) {
        LOGGER.info("Requested /api/geofences/{id} PUT method");
        if (!geofenceRepository.exists(id)) {
            throw new GeofenceNotFoundException();
        }
        geofence.setId(id);
        try {
            return geofenceRepository.save(geofence);
        } catch (Exception e) {
            throw new GeofenceResourceConflictException(id, e);
        }
    }

    /**
     * This method deletes the data of a previously created geofence.
     *
     * @param id unique identifier representing a specific geofence
     * @return the geofence deleted
     */
    @RequestMapping(path = "/api/geofences/{id}", method = RequestMethod.DELETE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Geofence deleted", response = Geofence.class),
            @ApiResponse(code = 404, message = "Geofence not found", response = GeofenceNotFoundException.class)})
    public Geofence deleteGeofence(@PathVariable("id") Long id) {
        LOGGER.info("Requested /api/geofences/{id} DELETE method");
        if (geofenceRepository.exists(id)) {
            geofenceRepository.delete(id);
            return null;
        } else {
            throw new GeofenceNotFoundException();
        }
    }
    /**
     * This method returns a geofence by id.
     *
     * @param id unique identifier representing a specific geofence
     * @return the geofence requested
     */
    @RequestMapping(path = "/api/geofences/{id}", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Geofence requested", response = Geofence.class),
            @ApiResponse(code = 404, message = "Geofence not found", response = GeofenceNotFoundException.class)})
    @JsonView(View.GeofenceCompleteView.class)
    public MappingJacksonValue getGeofence(@PathVariable("id") Long id) {
        LOGGER.info("Requested /api/geofences/{id} GET method");
        if (geofenceRepository.exists(id)) {
            final MappingJacksonValue result = new MappingJacksonValue(geofenceRepository.findOne(id));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if ((auth instanceof AnonymousAuthenticationToken)) {
                result.setSerializationView(View.GeofenceBaseView.class);
            } else {
                result.setSerializationView(View.GeofenceCompleteView.class);
            }
            return result;
        } else {
            throw new GeofenceNotFoundException();
        }
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Geofence status forbids request")
    public class GeofenceResourceConflictException extends RuntimeException {
        public GeofenceResourceConflictException(Long id, Exception e) {
            super("Failed update of Geofence "+id, e);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Geofence")
    public class GeofenceNotFoundException extends RuntimeException { }

}