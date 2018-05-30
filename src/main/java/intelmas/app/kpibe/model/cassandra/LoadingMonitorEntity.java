package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table("loading_monitor")
public class LoadingMonitorEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325328907318698346L;

	@PrimaryKey
	private LoadingMonitorEntityKey pk;
	
	public LoadingMonitorEntity() {
		this.pk = new LoadingMonitorEntityKey();
	}
	
	public void setPk(LoadingMonitorEntityKey pk) {
		this.pk = pk;
	}
	
	public String getOrganisation() {
		return this.pk.getOrganisation();
	}
	
	public String getDate() {
		return this.pk.getDate();
	}
	
	public String getName() {
		return this.pk.getName();
	}
	
	public String getParameter() {
		return this.pk.getParameter();
	}
	
	public int getHour() {
		return this.pk.getHour();
	}
	
	public int getMinute() {
		return this.pk.getMinute();
	}
}
