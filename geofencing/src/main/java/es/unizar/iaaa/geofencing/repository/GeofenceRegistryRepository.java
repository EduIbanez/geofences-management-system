package es.unizar.iaaa.geofencing.repository;

import es.unizar.iaaa.geofencing.model.GeofenceRegistry;
import org.springframework.data.repository.CrudRepository;

public interface GeofenceRegistryRepository extends CrudRepository<GeofenceRegistry, Long> {

    GeofenceRegistry findFirstByNickOrderByDateDesc(String nick);
}