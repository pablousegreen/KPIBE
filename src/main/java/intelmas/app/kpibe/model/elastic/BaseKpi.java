package intelmas.app.kpibe.model.elastic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class BaseKpi {

	private String name;
	private Map<String, Double> parameterValue;
	private Double calculation;
	
	public BaseKpi() {
		this.parameterValue = new HashMap<String, Double>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, Double> getParameterValue() {
		return this.parameterValue;
	}
	
	
	public Map<String, Double> decodeParameterValue() {
		if(this.parameterValue == null || this.parameterValue.size() == 0) return this.parameterValue;
		
		Map<String, Double> decodedParameterValue = new HashMap<String, Double>();
		for(String encodedKey: this.parameterValue.keySet()){
			Double value = this.parameterValue.get(encodedKey);
			String decodedKey = this.decodeKey(encodedKey);
			decodedParameterValue.put(decodedKey, value);
		}
		
		return decodedParameterValue;
	}
	
	
	public void setParameterValue(Map<String, Double> parameterValue) {
		if(parameterValue == null) {
			this.parameterValue = parameterValue;
			return;
		}
		
		for(String key: parameterValue.keySet()){
			Double value = parameterValue.get(key);
			String encodedKey = this.encodeKey(key);
			this.parameterValue.put(encodedKey, value);
		}
	}
	
	private String encodeKey(String key){
		return StringUtils.replace(key, ".", "-");
	}
	
	
	private String decodeKey(String encodedKey){
		return StringUtils.replace(encodedKey, "-", ".");
	}
	
	
	public Double getCalculation() {
		return calculation;
	}
	
	public void setCalculation(Double calculation) {
		this.calculation = calculation;
	}
	
}
