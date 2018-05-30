package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.UserDefinedType;

import com.datastax.driver.core.DataType.Name;

import intelmas.app.kpibe.constant.Aggregation;
import intelmas.app.kpibe.controller.dto.model.Formula;
import intelmas.app.kpibe.controller.dto.model.FormulaParameter;

@UserDefinedType("formula_mappings")
public class FormulaEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5012652801368923321L;

	@CassandraType(type = Name.TEXT)
	private String formula;
	
	@CassandraType(type = Name.MAP, typeArguments = { Name.TEXT, Name.TEXT })
	private Map<String, String> parametermapping;
	
	@CassandraType(type = Name.MAP, typeArguments = { Name.TEXT, Name.TEXT })
	private Map<String, String> aggregationmapping;
	
	public FormulaEntity() {
		this.parametermapping = new HashMap<>();
		this.aggregationmapping = new HashMap<>();
	}
	
	public String getFormula() {
		return formula;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public Map<String, String> getAggregationmapping() {
		return aggregationmapping;
	}
	
	public void setAggregationmapping(Map<String, String> aggregationmapping) {
		this.aggregationmapping = aggregationmapping;
	}
	
	public Map<String, String> getParametermapping() {
		return parametermapping;
	}
	
	public void setParametermapping(Map<String, String> parametermapping) {
		this.parametermapping = parametermapping;
	}
	
	public Formula toModel() {
		Formula formula = new Formula();
		formula.setFormula(this.getFormula());
		
		if(this.aggregationmapping != null && this.parametermapping != null) {
			Set<FormulaParameter> parameterSet = this.parametermapping.keySet().stream()
				.map(
					code -> {
						String name = this.parametermapping.get(code);
						if(name == null) return null;
						String aggregation = this.aggregationmapping.get(name);
						return new FormulaParameter(name, Aggregation.fromString(aggregation));
					})
				.filter(parameter -> parameter != null)
				.collect(Collectors.toSet());
			formula.setParameters( parameterSet );
		}
		
		return formula;
	}
}
