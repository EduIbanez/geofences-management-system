package es.unizar.iaaa.geofencing.repository;

import es.unizar.iaaa.geofencing.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}