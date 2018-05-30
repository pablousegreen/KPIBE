package intelmas.app.kpibe.repository.cassandra;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intelmas.app.kpibe.model.cassandra.CounterIdByNameEntity;
import intelmas.app.kpibe.model.cassandra.CounterIdByNameEntityKey;

public interface CounterIdByNameRepository extends CrudRepository<CounterIdByNameEntity, CounterIdByNameEntityKey>{
	
	@Query("SELECT * FROM counter_id_by_name WHERE organisation = :organisation AND name = :name limit 1")
	CounterIdByNameEntity findByOrganisationAndName(@Param("organisation") String organisation, @Param("name") String name);
	
}
