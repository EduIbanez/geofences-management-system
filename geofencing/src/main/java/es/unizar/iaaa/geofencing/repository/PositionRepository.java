package es.unizar.iaaa.geofencing.repository;

import es.unizar.iaaa.geofencing.domain.Position;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PositionRepository extends CrudRepository<Position, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u, Position p" +
            " WHERE p.id = ?1 AND u.email = ?2 AND u.id = p.user.id")
    Boolean existsByUsername(Long id, String username);
}