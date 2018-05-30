package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table("mos_by_node")
public class MosByNodeEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557134726953605780L;
	
	@PrimaryKey
	private MosByNodeEntityKey pk;
	
	public MosByNodeEntity() {
	}
	
	public MosByNodeEntity(DataSourceEntity dataSource) {
		if(dataSource == null) return;
		this.pk = new MosByNodeEntityKey(dataSource.getNode(), dataSource.getMoid());
	}
	
	public MosByNodeEntity(String node, String moid) {
		this.pk = new MosByNodeEntityKey(node, moid);
	}
	
	public MosByNodeEntity(MosByNodeEntityKey key){
		this.pk = key;
	}
	
	public String getNode(){
		return this.pk.getNode();
	}
	
	public String getMoid(){
		return this.pk.getMoid();
	}
	
}
