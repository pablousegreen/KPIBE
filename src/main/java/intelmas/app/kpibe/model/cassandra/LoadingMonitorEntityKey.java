package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class LoadingMonitorEntityKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325328907318698346L;
	
	@PrimaryKeyColumn(name = "organisation", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String organisation;

	@PrimaryKeyColumn(name = "date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String date;
	
	@PrimaryKeyColumn(name = "name", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private String name;
	
	@PrimaryKeyColumn(name = "parameter", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private String parameter;
	
	@PrimaryKeyColumn(name = "hour", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private int hour;
	
	@PrimaryKeyColumn(name = "minute", ordinal = 5, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private int minute;
	
	public LoadingMonitorEntityKey() {
	}
	
	public LoadingMonitorEntityKey(String organisation, String date, String name) {
		this.setOrganisation(organisation);
		this.setDate(date);
		this.setName(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getParameter() {
		return parameter;
	}
	
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	
	public String getOrganisation() {
		return organisation;
	}
	
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	
	public String toString(){
		StringBuilder idBuilder = new StringBuilder();
		idBuilder
			.append(this.getOrganisation()).append("-;-")
			.append(this.getDate()).append("-;-")
			.append(this.getName()).append("-;-")
			.append(this.getParameter()).append("-;-")
			.append(this.getHour()).append("-;-")
			.append(this.getMinute());
		
		return idBuilder.toString();
	}
	

}
