package es.unizar.iaaa.geofencing.repository;

import es.unizar.iaaa.geofencing.domain.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u, Notification n" +
            " WHERE n.id = ?1 AND u.email = ?2 AND u.id = n.user.id")
    Boolean existsByUsername(Long id, String username);
}