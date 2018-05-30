package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import com.datastax.driver.core.DataType.Name;


@Table("hourly_kpis")
public class HourlyKpiEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325328907318698346L;

	@PrimaryKey
	private final HourlyKpiEntityKey pk;
	
	private String node;
	
	private Double value;
	
	@CassandraType(type = Name.INT)
	private int counter;
	
	private Map<String, Double> parameters;
	
	public HourlyKpiEntity() {
		this.pk = new HourlyKpiEntityKey();
	}
	
	public HourlyKpiEntity(HourlyKpiEntityKey pk) {
		this.pk = pk;
	}
	
	public HourlyKpiEntityKey getPk() {
		return pk;
	}
	
	public String getOrganisation() {
		return this.pk.getOrganisation();
	}
	
	public String getDatehour() {
		return this.pk.getDatehour();
	}
	
	public String getName() {
		return this.pk.getName();
	}
	
	public String getOss() {
		return this.pk.getOss();
	}
	
	public void addAverage(HourlyKpiEntity savedEntity){
		if(savedEntity == null) return;
		System.out.println("savedEntity.getValue:" + savedEntity.getValue() + ", this.value:" + this.value + ", counter:" + savedEntity.getCounter() );
		this.value = ( (savedEntity.getValue() * savedEntity.getCounter() ) + this.value) / (savedEntity.getCounter() + 1);
		if(this.parameters != null && savedEntity.getParameters() != null){
			Map<String, Double> entityParameters = savedEntity.getParameters();
			this.parameters = this.parameters.keySet().stream()
				.collect(
					Collectors.toMap( 
						e -> e, 
						e -> (this.parameters.get(e) + (entityParameters.get(e) * savedEntity.getCounter())) /  (savedEntity.getCounter() + 1)
					));
		}
	}
	
	public String getNode() {
		return node;
	}
	
	public void setNode(String node) {
		this.node = node;
	}
	
	public String getMoid() {
		return this.pk.getMoid();
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public Map<String, Double> getParameters() {
		return parameters;
	}
	
	public void setParameters(Map<String, Double> parameters) {
		this.parameters = parameters;
	}
}
