package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Table("data_source")
public class DataSourceEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557134726953605780L;
	
	@PrimaryKeyColumn(name = "organisation", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String organisation;
	
	@PrimaryKeyColumn(name = "datetime", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private Timestamp datetime;
	
	@PrimaryKeyColumn(name = "node", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private String node;
	
	@PrimaryKeyColumn(name = "moid", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private String moid;
	
	private String oss;
	
	private Timestamp uploadtime;
	
	private Map<String, String> properties;
	
	public String getOrganisation() {
		return organisation;
	}
	
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
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

	public String getOss() {
		return oss;
	}

	public void setOss(String oss) {
		this.oss = oss;
	}
	
	public Timestamp getUploadtime() {
		return uploadtime;
	}
	
	public void setUploadtime(Timestamp uploadtime) {
		this.uploadtime = uploadtime;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
