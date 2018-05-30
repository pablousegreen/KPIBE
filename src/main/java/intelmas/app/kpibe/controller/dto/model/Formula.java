package intelmas.app.kpibe.controller.dto.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import intelmas.app.kpibe.model.cassandra.CounterIdByNameEntity;
import intelmas.app.kpibe.model.cassandra.FormulaEntity;
import intelmas.app.kpibe.repository.cassandra.CounterIdByNameRepository;

public class Formula implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5012652801368923321L;

	private String formula;
	
	private Set<FormulaParameter> parameters;
	
	public Formula() {
		this.parameters = ConcurrentHashMap.newKeySet();
	}
	
	public String getFormula() {
		return formula;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public Set<FormulaParameter> getParameters() {
		return parameters;
	}
	
	public void setParameters(Set<FormulaParameter> parameters) {
		this.parameters = parameters;
	}
	/*
	public void addParameters(Collection<String> parameters){
		this.parameters.addAll(parameters);
	}
	*/
	
	public FormulaEntity generateFormulaEntity(String organisation, CounterIdByNameRepository counterIdByNameRepository){
		if(organisation == null || this.parameters == null || counterIdByNameRepository == null) return null;
		
		FormulaEntity formulaEntity = new FormulaEntity();
		formulaEntity.setFormula(this.getFormula());
		
		Map<String, String> parameterMapping = new HashMap<>();
		Map<String, String> aggregationMapping = new HashMap<>();
		this.parameters.forEach(parameter -> {
			CounterIdByNameEntity counterEntity = counterIdByNameRepository.findByOrganisationAndName(organisation, parameter.getName());
			String counterId = parameter.getName();
			if(counterEntity != null) counterId = counterEntity.getId();
			parameterMapping.put(counterId, parameter.getName());
			aggregationMapping.put(parameter.getName(), parameter.getAggregation().getName());
			
		});
		
		formulaEntity.setParametermapping(parameterMapping);
		formulaEntity.setAggregationmapping(aggregationMapping);
		return formulaEntity;
	}
}
