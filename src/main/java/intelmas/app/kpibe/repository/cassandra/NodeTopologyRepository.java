package intelmas.app.kpibe.repository.cassandra;

import org.springframework.data.repository.CrudRepository;

import intelmas.app.kpibe.model.cassandra.NodeTopologyEntity;

public interface NodeTopologyRepository extends CrudRepository<NodeTopologyEntity, String>{
	
}
