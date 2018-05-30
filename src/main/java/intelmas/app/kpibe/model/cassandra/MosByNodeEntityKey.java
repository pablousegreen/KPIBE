package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class MosByNodeEntityKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325328907318698346L;
	
	@PrimaryKeyColumn(name = "node", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String node;

	@PrimaryKeyColumn(name = "moid", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private String moid;
	
	public MosByNodeEntityKey(String node, String moid) {
		this.setNode(node);
		this.setMoid(moid);
	}
	
	public String getMoid() {
		return moid;
	}
	
	public void setMoid(String moid) {
		this.moid = moid;
	}
	
	public String getNode() {
		return node;
	}
	
	public void setNode(String node) {
		this.node = node;
	}

}
