package intelmas.app.kpibe.model.elastic;

public class KpiParameter {

	private String name;
	private Double value;
	
	public KpiParameter() {
	}
	
	public KpiParameter(Object name, Object value) {
		if( !(name instanceof String) || !(value instanceof Double)) return;
		this.setName((String)name);
		this.setValue((Double)value);
	}
	
	public KpiParameter(String name, Double value) {
		this.setName(name);
		this.setValue(value);
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/*
	private String encodeKey(String key){
		return StringUtils.replace(key, ".", "-");
	}
	
	private String decodeKey(String encodedKey){
		return StringUtils.replace(encodedKey, "-", ".");
	}
	*/
	
}
