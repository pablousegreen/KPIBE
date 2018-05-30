package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.UserDefinedType;

import com.datastax.driver.core.DataType.Name;

@UserDefinedType("threshold")
public class Threshold implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5012652801368923321L;

	@CassandraType(type = Name.TEXT)
	private String type;
	
	@CassandraType(type = Name.DOUBLE)
	private Double value;
	
	public Threshold(){}
	
	public Threshold(String type, Double value) {
		this.setType(type);
		this.setValue(value);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
}
