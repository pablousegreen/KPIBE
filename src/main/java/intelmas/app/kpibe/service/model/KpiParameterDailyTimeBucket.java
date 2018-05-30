package intelmas.app.kpibe.service.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class KpiParameterDailyTimeBucket {
	
	private String name;
	
	private Map<Integer, HourTimeBucket> hourTimeBuckets;
	
	public KpiParameterDailyTimeBucket() {
		hourTimeBuckets = new LinkedHashMap<Integer, HourTimeBucket>();
		hourTimeBuckets.put(0, new HourTimeBucket());
		hourTimeBuckets.put(1, new HourTimeBucket());
		hourTimeBuckets.put(2, new HourTimeBucket());
		hourTimeBuckets.put(3, new HourTimeBucket());
		hourTimeBuckets.put(4, new HourTimeBucket());
		hourTimeBuckets.put(5, new HourTimeBucket());
		hourTimeBuckets.put(6, new HourTimeBucket());
		hourTimeBuckets.put(7, new HourTimeBucket());
		hourTimeBuckets.put(8, new HourTimeBucket());
		hourTimeBuckets.put(9, new HourTimeBucket());
		hourTimeBuckets.put(10, new HourTimeBucket());
		hourTimeBuckets.put(11, new HourTimeBucket());
		hourTimeBuckets.put(12, new HourTimeBucket());
		hourTimeBuckets.put(13, new HourTimeBucket());
		hourTimeBuckets.put(14, new HourTimeBucket());
		hourTimeBuckets.put(15, new HourTimeBucket());
		hourTimeBuckets.put(16, new HourTimeBucket());
		hourTimeBuckets.put(17, new HourTimeBucket());
		hourTimeBuckets.put(18, new HourTimeBucket());
		hourTimeBuckets.put(19, new HourTimeBucket());
		hourTimeBuckets.put(20, new HourTimeBucket());
		hourTimeBuckets.put(21, new HourTimeBucket());
		hourTimeBuckets.put(22, new HourTimeBucket());
		hourTimeBuckets.put(23, new HourTimeBucket());
	}
	
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Map<Integer, HourTimeBucket> getHourTimeBuckets() {
		return hourTimeBuckets;
	}
	
	public void setHourTimeBuckets(Map<Integer, HourTimeBucket> hourTimeBuckets) {
		this.hourTimeBuckets = hourTimeBuckets;
	}
	
	public void updateHourTimeBucket(Integer hour, Integer minute){
		HourTimeBucket hourTimeBucket = hourTimeBuckets.get(hour);
		hourTimeBucket.updateStatus(minute);
	}
	
}
