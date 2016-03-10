package es.unizar.iaaa.geofencing.web;

import com.google.common.collect.Lists;
import es.unizar.iaaa.geofencing.model.Geofence;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeofenceController {

    @RequestMapping(path="/api/geofences", method= RequestMethod.GET)
    public List<Geofence> getGeofences() {
        return Lists.newArrayList(new Geofence(1), new Geofence(2), new Geofence(3));
    }

}