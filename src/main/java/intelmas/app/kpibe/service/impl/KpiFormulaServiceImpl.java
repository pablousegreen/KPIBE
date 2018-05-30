package intelmas.app.kpibe.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import intelmas.app.kpibe.controller.dto.BaseDTO;
import intelmas.app.kpibe.controller.dto.KpiFormulaDTO;
import intelmas.app.kpibe.controller.dto.KpiFormulasDTO;
import intelmas.app.kpibe.controller.dto.model.KpiFormula;
import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntityKey;
import intelmas.app.kpibe.repository.cassandra.CounterIdByNameRepository;
import intelmas.app.kpibe.repository.cassandra.KpiFormulaRepository;
import intelmas.app.kpibe.service.KpiFormulaService;

@Service
public class KpiFormulaServiceImpl implements KpiFormulaService {
	
	private KpiFormulaRepository kpiFormulaRepository;
	private CounterIdByNameRepository counterIdByNameRepository;
	
	public KpiFormulaServiceImpl(KpiFormulaRepository kpiFormulaRepository, CounterIdByNameRepository counterIdByNameRepository) {
		this.kpiFormulaRepository = kpiFormulaRepository;
		this.counterIdByNameRepository = counterIdByNameRepository;
	}
	
	@Override
	public KpiFormulasDTO getKpiFormulas(String organisation) throws ProcessingException {
		// String organisation = "intelmas"; // hardcoded
		if(StringUtils.isBlank(organisation))
			organisation = "intelmas";
		Iterable<KpiFormulaEntity> entities = kpiFormulaRepository.findByOrganisation(organisation);
		
		KpiFormulasDTO dto = new KpiFormulasDTO();
		dto.setFromEntities(entities);
		
        return dto;
	}

	@Override
	public BaseDTO createKpiFormula(KpiFormula kpiFormula) throws ProcessingException {
		kpiFormulaRepository.save(kpiFormula.generateEntity(counterIdByNameRepository));
		
		return new BaseDTO("0000", "OK");
	}

	@Override
	public Iterable<KpiFormulaEntity> getKpiFormulasByCategory(String organisation, String category) throws ProcessingException {
		if(StringUtils.isBlank(organisation))
			organisation = "intelmas";
		
		Iterable<KpiFormulaEntity> entities = kpiFormulaRepository.findByOrganisationAndCategory(organisation, category);
		
		KpiFormulasDTO dto = new KpiFormulasDTO();
		dto.setFromEntities(entities);
		
        return entities;
	}

	@Override
	public KpiFormulaDTO getKpiFormulasById(String id) throws ProcessingException {

		KpiFormulaEntityKey key = new KpiFormulaEntityKey(id);
		
		KpiFormulaEntity entity = kpiFormulaRepository.findOne(key);
		if(entity == null) throw new ProcessingException("1300", "Id cannot be found");
		
		KpiFormulaDTO dto = new KpiFormulaDTO();
		dto.setKpiFormula(entity.generateKpiFormula());
		return dto;
	}

	@Override
	public BaseDTO deleteKpiFormula(KpiFormula kpiFormula) throws ProcessingException {
		if(StringUtils.isBlank(kpiFormula.getId())) throw new ProcessingException("2000", "Invalid Kpi Formula ID");
		KpiFormulaEntityKey key = new KpiFormulaEntityKey(kpiFormula.getId());
		
		kpiFormulaRepository.delete(key);
		return new BaseDTO("0000", "OK");
	}
	
	
	

}
