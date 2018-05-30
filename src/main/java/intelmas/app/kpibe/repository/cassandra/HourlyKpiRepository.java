package intelmas.app.kpibe.repository.cassandra;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intelmas.app.kpibe.model.cassandra.HourlyKpiEntity;
import intelmas.app.kpibe.model.cassandra.HourlyKpiEntityKey;

public interface HourlyKpiRepository extends CrudRepository<HourlyKpiEntity, HourlyKpiEntityKey>{
	
	@Query("SELECT * FROM hourly_kpis WHERE organisation = :organisation AND datehour = :datehour AND oss = :oss AND name = :name")
	Iterable <HourlyKpiEntity> findByKey(
			@Param("organisation") String organisation,
			@Param("datehour") String datehour,
			@Param("oss") String oss,
			@Param("name") String name);
	
}
