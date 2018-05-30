package intelmas.app.kpibe.controller.dto.model;

import java.io.Serializable;

import intelmas.app.kpibe.constant.Aggregation;

public class FormulaParameter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5012652801368923321L;

	private String name;
	
	private Aggregation aggregation;
	
	public FormulaParameter() {	}
	
	public FormulaParameter(String name, Aggregation aggregation) {
		this.setName(name);
		this.setAggregation(aggregation);
	}
	
	public Aggregation getAggregation() {
		if(this.aggregation == null) return Aggregation.AVERAGE;
		return aggregation;
	}
	
	public void setAggregation(Aggregation aggregation) {
		this.aggregation = aggregation;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
