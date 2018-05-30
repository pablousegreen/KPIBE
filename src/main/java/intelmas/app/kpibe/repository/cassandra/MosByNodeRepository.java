package intelmas.app.kpibe.repository.cassandra;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import intelmas.app.kpibe.model.cassandra.MosByNodeEntity;
import intelmas.app.kpibe.model.cassandra.MosByNodeEntityKey;

public interface MosByNodeRepository extends CrudRepository<MosByNodeEntity, MosByNodeEntityKey>{
	
	@Query("SELECT * FROM mos_by_node WHERE node = :node")
	Iterable <MosByNodeEntity> findByNode(@Param("node") String node);
	
}
