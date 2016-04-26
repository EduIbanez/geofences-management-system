package es.unizar.iaaa.geofencing.repository;

import es.unizar.iaaa.geofencing.domain.Notification;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

}