package es.unizar.iaaa.geofencing.web;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;
import es.unizar.iaaa.geofencing.model.Geofence;
import es.unizar.iaaa.geofencing.model.Properties;
import es.unizar.iaaa.geofencing.model.User;
import es.unizar.iaaa.geofencing.repository.GeofenceRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class GeofenceController {

    @Autowired
    private GeofenceRepository geofenceRepository;

    private static final Logger logger = LoggerFactory.getLogger(GeofenceController.class);

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
    public ResponseEntity<Geofence> createGeofence(@RequestBody Geofence geofence) {
        String type = geofence.getType();
        Geometry geometry = geofence.getGeometry();
        Properties properties = geofence.getProperties();
        User user = geofence.getUser();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(5).toUri());
        return new ResponseEntity<>(new Geofence(5, type, properties, geometry, user), httpHeaders, HttpStatus.CREATED);
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
    public List<Geofence> getGeofences(@RequestParam(value = "limit", required = false) Integer limit,
                                       @RequestParam(value = "latitude", required = false) String latitude,
                                       @RequestParam(value = "longitude", required = false) String longitude,
                                       @RequestParam(value = "radius", required = false) Integer radius) {
        return Lists.newArrayList(createPolygonFixture(1), createPolygonFixture(2), createPolygonFixture(3));
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
            @ApiResponse(code = 304, message = "Geofence not modified", response = GeofenceNotModifiedException.class),
            @ApiResponse(code = 404, message = "Geofence not found", response = GeofenceNotFoundException.class)})
    public Geofence modifyGeofence(@PathVariable("id") int id, @RequestBody Geofence geofence) {
        String type = geofence.getType();
        Properties properties = geofence.getProperties();
        Geometry geometry = geofence.getGeometry();
        User user = geofence.getUser();
        if (id < 3) {
            return new Geofence(id, type, properties, geometry, user);
        } else if (id == 3) {
            throw new GeofenceNotModifiedException();
        } else {
            throw new GeofenceNotFoundException();
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
    // TODO Repasar reglas de HTTP
    public Geofence deleteGeofence(@PathVariable("id") int id) {
        if (id == 3) {
            return createPolygonFixture(id);
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
    public Geofence getGeofence(@PathVariable("id") int id) {
        if (id == 3) {
            return createPolygonFixture(id);
        } else {
            throw new GeofenceNotFoundException();
        }
    }

    private Geofence createPolygonFixture(@PathVariable("id") int id) {
        User user = new User(1, "example.gmail.com", "password", "First", "Last", "07/08/1992", "356938035643809", null);
        return new Geofence(id, "Feature", new Properties("Cuadrado"), null, user);
    }

    @ResponseStatus(value = HttpStatus.NOT_MODIFIED, reason = "Not modified")
    public class GeofenceNotModifiedException extends RuntimeException { }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Geofence")
    public class GeofenceNotFoundException extends RuntimeException { }

}