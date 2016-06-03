package es.unizar.iaaa.geofencing.repository;

import es.unizar.iaaa.geofencing.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.id = ?1 AND u.nick = ?2")
    Boolean existsByUsername(Long id, String username);

    @Query("SELECT u FROM User u WHERE u.nick = ?1")
    User findByUsername(String username);
}