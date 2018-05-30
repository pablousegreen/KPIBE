package intelmas.app.kpibe.service;

import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.MosByNodeEntity;

public interface MosByNodeService {

	public Iterable<MosByNodeEntity> getMosByNode(String node) throws ProcessingException;
}
