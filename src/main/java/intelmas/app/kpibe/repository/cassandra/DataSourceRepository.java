package intelmas.app.kpibe.repository.cassandra;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import intelmas.app.kpibe.model.cassandra.DataSourceEntity;

public interface DataSourceRepository extends CrudRepository<DataSourceEntity, UUID>{
	
}
