package es.unizar.iaaa.geofencing.repository;

import es.unizar.iaaa.geofencing.domain.Rule;
import org.springframework.data.repository.CrudRepository;

public interface RuleRepository extends CrudRepository<Rule, Long> {

}