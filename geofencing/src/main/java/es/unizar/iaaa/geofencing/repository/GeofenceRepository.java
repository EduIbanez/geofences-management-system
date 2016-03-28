package es.unizar.iaaa.geofencing.repository;

import com.vividsolutions.jts.geom.Geometry;
import es.unizar.iaaa.geofencing.model.Geofence;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeofenceRepository extends CrudRepository<Geofence, Long> {

    @Query("SELECT g FROM Geofence g WHERE within(g.geometry, ?1) = true")
    List<Geofence> findWithin(Geometry filter);
}