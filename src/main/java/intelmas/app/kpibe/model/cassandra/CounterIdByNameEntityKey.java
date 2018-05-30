package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class CounterIdByNameEntityKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325328907318698346L;
	
	@PrimaryKeyColumn(name = "organisation", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String organisation;

	@PrimaryKeyColumn(name = "name", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String name;
	
	public CounterIdByNameEntityKey(String organisation, String name) {
		this.setOrganisation(organisation);
		this.setName(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOrganisation() {
		return organisation;
	}
	
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
}
