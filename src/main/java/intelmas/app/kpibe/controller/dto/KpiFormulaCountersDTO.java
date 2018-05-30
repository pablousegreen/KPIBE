package intelmas.app.kpibe.controller.dto;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import intelmas.app.kpibe.model.cassandra.FormulaEntity;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;

public class KpiFormulaCountersDTO extends BaseDTO {

	private Set<String> counters;
	
	public KpiFormulaCountersDTO() {
		super("0000", "OK");
	}
	
	public Set<String> getCounters() {
		return counters;
	}
	
	public void setCounters(Set<String> counters) {
		this.counters = counters;
	}
	
	public void addCounters(Set<String> counters) {
		if(this.counters == null) this.counters = new LinkedHashSet<String>();
		this.counters.addAll(counters);
	}
	
	public void setFromEntities(Iterable<KpiFormulaEntity> entities){
		if(this.counters == null)  this.counters = new LinkedHashSet<String>();
		
		if(entities == null) return;
		
		Set<String> parameterName = StreamSupport.stream(entities.spliterator(), false)
			.map( entity -> {
				FormulaEntity formula = entity.getFormula();
				if(formula == null) return null;
				
				Map<String, String> parameters = formula.getParametermapping();
				if(parameters == null) return null;
				
				return parameters.values().stream().map(parameter -> parameter);
			})
			.filter( entity -> entity != null)
			.flatMap( parameter -> parameter)
			.collect(Collectors.toSet());
		
		this.counters.addAll(parameterName);
		
		/*
		entities.forEach( 
			item -> {
				FormulaEntity formula = item.getFormula();
				if(formula != null){
					formula.getParameters();
					this.counters.addAll(formula.getParameters().keySet());
				}
			}
		);
		*/
	}
}
