package es.unizar.iaaa.geofencing.repository;

import es.unizar.iaaa.geofencing.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id = ?1 AND u.email = ?2")
    User findOneByEmail(Long id, String email);
}