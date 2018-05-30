package intelmas.app.kpibe.controller;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import intelmas.app.kpibe.controller.dto.BaseDTO;
import intelmas.app.kpibe.controller.dto.KpiFormulaCountersDTO;
import intelmas.app.kpibe.controller.dto.KpiFormulaDTO;
import intelmas.app.kpibe.controller.dto.KpiFormulasDTO;
import intelmas.app.kpibe.controller.dto.model.KpiFormula;
import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;
import intelmas.app.kpibe.service.KpiFormulaService;


@RestController
@RequestMapping("/kpiFormulaApi")
public class KpiFormulaController extends BasicController {
	
	@Autowired
	private KpiFormulaService kpiFormulaService;

	@GetMapping("/getAllKpiFormulas")
    public KpiFormulasDTO getAllKpiFormulas(@RequestParam(value = "vendor", required = false) String vendor) throws ProcessingException {
		
		return kpiFormulaService.getKpiFormulas(vendor);
	}
	
	@GetMapping("/getStandardKpiFormulas")
    public KpiFormulasDTO getStandardKpiFormulas(@RequestParam(value = "vendor", required = false) String vendor) throws ProcessingException {
		
		KpiFormulasDTO dto = kpiFormulaService.getKpiFormulas(vendor);
		if(dto == null) return dto;
		
		Set<KpiFormula> kpiFormulas = dto.getKpiFormulas();
		if(kpiFormulas == null || kpiFormulas.size() <= 0) return dto;
		
		Set<KpiFormula> filteredKpiFormulas = new HashSet<KpiFormula>();
		kpiFormulas.forEach( kpiFormula -> {
			if(StringUtils.equalsIgnoreCase(kpiFormula.getType(), KpiFormula.TYPE_STANDARD))
				filteredKpiFormulas.add(kpiFormula);
		});
		
		dto.setKpiFormulas(filteredKpiFormulas);
		
		return dto;
	}
	
	@GetMapping("/getKpiFormulasByType")
    public KpiFormulasDTO getKpiFormulasByType(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String type) throws ProcessingException {
		
		Iterable<KpiFormulaEntity> entities = kpiFormulaService.getKpiFormulasByCategory(vendor, type);
		KpiFormulasDTO dto = new KpiFormulasDTO();
		dto.setFromEntities(entities);
		
		return dto;
	}
	
	@GetMapping("/getCountersByType")
    public KpiFormulaCountersDTO getCountersByType(
    		@RequestParam(value = "vendor", required = false) String vendor, 
    		String type) throws ProcessingException {
		
		Iterable<KpiFormulaEntity> entities = kpiFormulaService.getKpiFormulasByCategory(vendor, type);
		KpiFormulaCountersDTO dto = new KpiFormulaCountersDTO();
		dto.setFromEntities(entities);
		
		return dto; 
	}
	
	
	@GetMapping("/getKpiFormulasById")
    public KpiFormulaDTO getKpiFormulasById(String id) throws ProcessingException {
		return kpiFormulaService.getKpiFormulasById(id);
	}
	
	@PostMapping("/createKpiFormula")
    public BaseDTO createKpi(@RequestBody KpiFormula formula) throws ProcessingException {
		if(formula == null)
			throw new ProcessingException("2000", "Missing mandatory input parameters");
		
		return kpiFormulaService.createKpiFormula(formula);
	}
	
	@PostMapping("/deleteKpiFormula")
    public BaseDTO deleteKpiFormula(@RequestBody KpiFormula formula) throws ProcessingException {
		if(formula == null || formula.getId() == null)
			throw new ProcessingException("2000", "Missing mandatory input parameters");
		
		return kpiFormulaService.deleteKpiFormula(formula);
	}
	
}
