package intelmas.app.kpibe.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.MosByNodeEntity;
import intelmas.app.kpibe.service.MosByNodeService;


@RestController
@RequestMapping("/moApi")
public class MosByNodeController extends BasicController {
	
	private MosByNodeService mosByNodeService;
	
	public MosByNodeController(MosByNodeService mosByNodeService) {
		this.mosByNodeService = mosByNodeService;
	}
	
	
	@GetMapping("/getMosByNode")
    public Set<String> getMosByNode (String node) throws ProcessingException {
		
		if(StringUtils.isBlank(node)) throw new ProcessingException("2000", "node is missing");
		Iterable<MosByNodeEntity> entities = mosByNodeService.getMosByNode(node);
		if(entities == null) return new HashSet<String>();
		
		return StreamSupport.stream(entities.spliterator(), false).map(entity -> entity.getMoid()).collect(Collectors.toSet());
	}
}
