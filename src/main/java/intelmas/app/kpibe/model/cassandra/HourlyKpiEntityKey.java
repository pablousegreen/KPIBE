package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import intelmas.app.kpibe.tools.Utils;

@PrimaryKeyClass
public class HourlyKpiEntityKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325328907318698346L;
	
	@PrimaryKeyColumn(name = "organisation", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String organisation;

	@PrimaryKeyColumn(name = "datehour", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String datehour;
	
	@PrimaryKeyColumn(name = "oss", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private String oss;
	
	@PrimaryKeyColumn(name = "name", ordinal = 3, type = PrimaryKeyType.PARTITIONED)
	private String name;
	
	@PrimaryKeyColumn(name = "moid", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private String moid;
	
	public HourlyKpiEntityKey() {
	}
	
	
	public HourlyKpiEntityKey(String organisation, Timestamp datetime, String oss
			, String name, String moid) {
		this.setOrganisation(organisation);
		this.setDatetime(datetime);
		this.setName(name);
		this.setOss(oss);
		this.setMoid(moid);
	}
	
	public String getDatehour() {
		return datehour;
	}
	
	public void setDatehour(String datehour) {
		this.datehour = datehour;
	}
	
	public void setDatetime(Timestamp datetime){
		this.datehour = Utils.timestampToDateString(datetime);
	}
	
	public String getOss() {
		return oss;
	}
	
	public void setOss(String oss) {
		this.oss = oss;
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
	
	public String getMoid() {
		return moid;
	}
	
	public void setMoid(String moid) {
		this.moid = moid;
	}
	
}
