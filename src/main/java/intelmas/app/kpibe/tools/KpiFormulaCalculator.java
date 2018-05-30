package intelmas.app.kpibe.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.FormulaEntity;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class KpiFormulaCalculator {

	private Map<String, Double> parameterMap = new ConcurrentHashMap<String, Double>();
	private Expression expression;
	private String category;
	private String formulaName;
	private Double max;
	private KpiFormulaEntity kpiFormulaEntity;
	
	private static final Logger LOG = LoggerFactory.getLogger(KpiFormulaCalculator.class);
	
	public KpiFormulaCalculator(KpiFormulaEntity kpiFormulaEntity) throws ProcessingException{
		if(kpiFormulaEntity == null) return;
		
		FormulaEntity formula = kpiFormulaEntity.getFormula();
		if(formula == null || formula.getFormula() == null || formula.getParametermapping() == null) return;
		
		this.formulaName = kpiFormulaEntity.getName();
		this.max = kpiFormulaEntity.getMax();
		
		String formulaExpression = formula.getFormula();
		StringBuilder sb = new StringBuilder(formulaExpression);
		
		Map<String, String> parameters = formula.getParametermapping();
		
		Set<String> variables = new HashSet<String>();
		parameters.keySet().stream().forEach( parameter -> {
			String parameterName = parameters.get(parameter);
			if(parameterName == null) return;
			
			String replaceParam = StringUtils.replace(parameterName, ".", "_");
			sb.replace(0, sb.length(), StringUtils.replace(sb.toString(), parameterName, replaceParam));
			
			variables.add(replaceParam);
			parameterMap.put(parameter, -0.0);
		});
		
		try{
			this.expression = new ExpressionBuilder(sb.toString())
		        .variables(variables)
		        .build();
		}catch(Exception e) {
			LOG.info("Unable to parse the formula [FormulaName:{}][Exception:{}]", this.formulaName, e.toString());
			throw new ProcessingException("3000", "Unable to parse the formula [FormulaName:" + this.formulaName + "][Exception:" + e.toString() + "]");
		}
		
		this.setCategory(kpiFormulaEntity.getCategory());
		this.setKpiFormulaEntity(kpiFormulaEntity);
	}
	
	public void setParameter(String parameter, Double value) {
		if(!parameterMap.containsKey(parameter)) return;
		parameterMap.put(parameter, value);
	}
	
	public Set<String> getParameters(){
		return parameterMap.keySet();
	}
	
	public Map<String, Double> getParameterMap() {
		return parameterMap;
	}
	
	public Map<String, Double> getNamedParameterMap() {
		
		Map<String, String> formulaNameMap = this.getKpiFormulaEntity().getFormula().getParametermapping();
		
		Map<String, Double> namedParameterMap = new HashMap<String, Double>();
		this.parameterMap.forEach((key, value) -> {
			String namedParameter = formulaNameMap.get(key);
			if(namedParameter == null) namedParameter = key; // Anticipation if the map is null
			
			namedParameterMap.put(formulaNameMap.get(key), value);
		});
		
		return namedParameterMap;
	}
	
	public void setParameterMap(Map<String, Double> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public KpiFormulaEntity getKpiFormulaEntity() {
		return kpiFormulaEntity;
	}
	
	public void setKpiFormulaEntity(KpiFormulaEntity kpiFormulaEntity) {
		this.kpiFormulaEntity = kpiFormulaEntity;
	}
	
	public String getFormulaName() {
		return formulaName;
	}
	
	public void setFormulaName(String formulaName) {
		this.formulaName = formulaName;
	}
	
	public Double getMax() {
		return max;
	}
	
	public void setMax(Double max) {
		this.max = max;
	}
	
	public double calculate(){
		
		if(this.expression == null) return 0;
		
		Map<String, Double> namedParameterMap = getNamedParameterMap();
		for(String parameter: namedParameterMap.keySet()){
			String replaceParam = StringUtils.replace(parameter, ".", "_");
			
			Double value = parameterMap.get(parameter);
			this.expression.setVariable(replaceParam, value);
		}
		
		try{
			double result = this.expression.evaluate();
			if(this.max != null && result > this.max) result = this.max;
			else if(Double.isNaN(result)) result = 0;
			else if(result < 0) return -0.0;
			return result;
		}catch(Exception ex){
			// LOG.info("Unable to calculate the formula [FormulaName:{}][Exception:{}]. Calculation result is assumed to be 0", this.getFormulaName(), ex.toString());
			return 0;
		}
	}
}
