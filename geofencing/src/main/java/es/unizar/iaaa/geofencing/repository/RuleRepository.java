package es.unizar.iaaa.geofencing.repository;

import es.unizar.iaaa.geofencing.domain.Rule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RuleRepository extends CrudRepository<Rule, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u, Rule r" +
            " WHERE r.id = ?1 AND u.email = ?2 AND u.id = r.geofence.user.id")
    Boolean existsByUsername(Long id, String username);
}