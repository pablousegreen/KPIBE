package intelmas.app.kpibe.controller.dto.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import intelmas.app.kpibe.service.model.KpiBucket;
import intelmas.app.kpibe.service.model.KpiParameterDailyTimeBucket;

public class KpiStatus {

	private String name;
	private List<KpiParameterStatus> parameters = new ArrayList<KpiParameterStatus>();
	
	public KpiStatus(){
	}
	
	public KpiStatus(String name){
		this.setName(name);
	}
	
	public KpiStatus(KpiBucket kpiBucket){
		this.setName(kpiBucket.getName());
		
		Map<String, KpiParameterDailyTimeBucket> parameterBuckets = kpiBucket.getParameterDailyBucket();
		for(String parameter: parameterBuckets.keySet()){
			KpiParameterStatus parameterStatus = new KpiParameterStatus();
			parameterStatus.setName(parameter);
			parameterStatus.updateParamStatuses(parameterBuckets.get(parameter));
			parameters.add(parameterStatus);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<KpiParameterStatus> getParameters() {
		return parameters;
	}
	
	public void setParameters(List<KpiParameterStatus> parameters) {
		this.parameters = parameters;
	}
}
