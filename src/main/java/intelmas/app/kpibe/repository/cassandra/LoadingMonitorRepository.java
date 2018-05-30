package intelmas.app.kpibe.repository.cassandra;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intelmas.app.kpibe.model.cassandra.LoadingMonitorEntity;
import intelmas.app.kpibe.model.cassandra.LoadingMonitorEntityKey;

public interface LoadingMonitorRepository extends CrudRepository<LoadingMonitorEntity, LoadingMonitorEntityKey>{
	
	@Query("SELECT * FROM loading_monitor WHERE organisation = :organisation AND date = :date")
	Iterable <LoadingMonitorEntity> findByOrganisationAndDate(@Param("organisation") String organisation, @Param("date") String date);
	
}
