package intelmas.app.kpibe.service.model;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import intelmas.app.kpibe.tools.Utils;

public class KpiBucket {

	private String name;
	private Map<String, KpiParameterDailyTimeBucket> parameterDailyBucket;
	
	public KpiBucket() {
		parameterDailyBucket = new HashMap<String, KpiParameterDailyTimeBucket>();
	}
	
	public KpiBucket(String name){
		this.setName(name);
		parameterDailyBucket = new HashMap<String, KpiParameterDailyTimeBucket>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, KpiParameterDailyTimeBucket> getParameterDailyBucket() {
		return parameterDailyBucket;
	}
	
	public void setParameterDailyBucket(Map<String, KpiParameterDailyTimeBucket> parameterDailyBucket) {
		this.parameterDailyBucket = parameterDailyBucket;
	}
	
	public void addParameter(String paramName, Integer hour, Integer minute){
		KpiParameterDailyTimeBucket timeBucket = parameterDailyBucket.get(paramName);
		if(timeBucket == null){
			timeBucket = new KpiParameterDailyTimeBucket();
			parameterDailyBucket.put(paramName, timeBucket);
		}
		
		timeBucket.updateHourTimeBucket(hour, minute);
	}
	
	public void updateParameterDailyBucket(String paramName, String millisecondString){
		try {
			KpiParameterDailyTimeBucket timeBucket = parameterDailyBucket.get(paramName);
			if(timeBucket == null){
				timeBucket = new KpiParameterDailyTimeBucket();
				parameterDailyBucket.put(paramName, timeBucket);
			}
			
			Long millisecondTimestamp = Long.parseLong(millisecondString);
			Instant instant = Instant.ofEpochMilli(millisecondTimestamp);
			
			ZonedDateTime datetime = instant.atZone(Utils.TIMEZONE);
			int hour = datetime.getHour();
			int minute = datetime.getMinute();
			timeBucket.updateHourTimeBucket(hour, minute);
			
		}catch(Exception e){
			// Invalid value, assume data is missing
		}
		
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("Kpi Name:").append(this.getName()).append("\n");
		for(String key: parameterDailyBucket.keySet()){
			sb.append("Parameter:").append(key);
			KpiParameterDailyTimeBucket timeBucket = parameterDailyBucket.get(key);
			Map<Integer, HourTimeBucket> hourTimeBuckets = timeBucket.getHourTimeBuckets();
			for(Integer hourKey: hourTimeBuckets.keySet()){
				sb.append(hourKey).append(":").append(hourTimeBuckets.get(hourKey).getStatus());
			}
			
			sb.append("\n");
		}
		
		
		return sb.toString();
	}
}
