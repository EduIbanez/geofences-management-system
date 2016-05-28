package es.unizar.iaaa.geofencing.repository;

import com.vividsolutions.jts.geom.Geometry;
import es.unizar.iaaa.geofencing.model.Geofence;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeofenceRepository extends CrudRepository<Geofence, Long> {

    @Query("SELECT g FROM Geofence g WHERE g.user.email = ?1 ORDER BY g.id DESC")
    List<Geofence> find(String email);

    @Query("SELECT g FROM Geofence g WHERE within(g.geometry, ?1) = true")
    List<Geofence> findWithin(Geometry filter);

    @Query("SELECT g FROM Geofence g WHERE within(g.geometry, ?1) = true")
    List<Geofence> findWithin(Geometry filter, Pageable pageable);

    @Query("SELECT g FROM Geofence g WHERE within(?1, g.geometry) = true AND g.user.email = ?2")
    List<Geofence> findWithin(Geometry filter, String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u, Geofence g" +
            " WHERE g.id = ?1 AND u.email = ?2 AND u.id = g.user.id")
    Boolean existsByUsername(Long id, String username);
}