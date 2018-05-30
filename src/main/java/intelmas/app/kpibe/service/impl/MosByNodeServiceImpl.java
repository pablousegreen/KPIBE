package intelmas.app.kpibe.service.impl;

import org.springframework.stereotype.Service;

import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.MosByNodeEntity;
import intelmas.app.kpibe.repository.cassandra.MosByNodeRepository;
import intelmas.app.kpibe.service.MosByNodeService;

@Service
public class MosByNodeServiceImpl implements MosByNodeService {
	
	private final MosByNodeRepository mosByNodeRepository;
	
	public MosByNodeServiceImpl(MosByNodeRepository mosByNodeRepository) {
		this.mosByNodeRepository = mosByNodeRepository;
	}

	@Override
	public Iterable<MosByNodeEntity> getMosByNode(String node) throws ProcessingException {
		return mosByNodeRepository.findByNode(node);
	}
	
}
