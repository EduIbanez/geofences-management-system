package es.unizar.iaaa.geofencing.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import es.unizar.iaaa.geofencing.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.nick = ?1")
    Boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.nick = ?1")
    User findByUsername(String username);

    @Modifying
    @Query("DELETE FROM User u WHERE u.nick = ?1")
    @Transactional
    void delete(String username);
}