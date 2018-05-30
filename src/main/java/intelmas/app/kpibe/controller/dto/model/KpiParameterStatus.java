package intelmas.app.kpibe.controller.dto.model;

import java.util.LinkedHashMap;
import java.util.Map;

import intelmas.app.kpibe.service.model.HourTimeBucket;
import intelmas.app.kpibe.service.model.KpiParameterDailyTimeBucket;

public class KpiParameterStatus {

	private String name;
	private Map<Integer, String> paramStatuses = new LinkedHashMap<Integer, String>();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Map<Integer, String> getParamStatuses() {
		return paramStatuses;
	}
	
	public void setParamStatuses(Map<Integer, String> paramStatuses) {
		this.paramStatuses = paramStatuses;
	}
	
	public void updateParamStatuses(KpiParameterDailyTimeBucket timeBucket){
		if(timeBucket == null) return;
		
		Map<Integer, HourTimeBucket> hourTimeBuckets = timeBucket.getHourTimeBuckets();
		for(Integer hour: hourTimeBuckets.keySet()){
			paramStatuses.put(hour, hourTimeBuckets.get(hour).getStatus());
		}
	}
}
