package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table("counter_id_by_name")
public class CounterIdByNameEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557134726953605780L;
	
	@PrimaryKey
	private CounterIdByNameEntityKey pk;
	
	private String id;
	
	public CounterIdByNameEntity() {
	}
	
	public CounterIdByNameEntity(String organisation, String name) {
		this.pk = new CounterIdByNameEntityKey(organisation, name);
	}
	
	public CounterIdByNameEntity(CounterIdByNameEntityKey key){
		this.pk = key;
	}
	
	public String getOrganisation(){
		return this.pk.getOrganisation();
	}
	
	public String getName(){
		return this.pk.getName();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
}
