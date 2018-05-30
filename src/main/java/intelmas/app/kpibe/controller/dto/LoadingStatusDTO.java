package intelmas.app.kpibe.controller.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import intelmas.app.kpibe.controller.dto.model.KpiStatus;
import intelmas.app.kpibe.model.cassandra.LoadingMonitorEntity;
import intelmas.app.kpibe.service.model.KpiBucket;
import intelmas.app.kpibe.service.model.KpiBucketCollector;

public class LoadingStatusDTO extends BaseDTO {

	private Set<KpiStatus> kpiStatuses;
	
	public LoadingStatusDTO() {
		super("0000", "OK");
		kpiStatuses = new HashSet<KpiStatus>();
	}
	
	public Set<KpiStatus> getKpiStatuses() {
		return kpiStatuses;
	}
	
	public void setKpiStatuses(Set<KpiStatus> kpiStatuses) {
		this.kpiStatuses = kpiStatuses;
	}
	
	public void updateFromBucket(List<KpiBucket> kpiBuckets){
		kpiStatuses = new HashSet<KpiStatus>();
		if(kpiBuckets == null) return;
		
		for(KpiBucket kpiBucket: kpiBuckets) {
			KpiStatus kpiStatus = new KpiStatus(kpiBucket);
			kpiStatuses.add(kpiStatus);
		}
	}
	
	public static LoadingStatusDTO generateLoadingStatus(Iterable<LoadingMonitorEntity> loadingMonitors){
		
		LoadingStatusDTO dto = new LoadingStatusDTO();
		if(loadingMonitors == null) return dto;
		
		KpiBucketCollector collector = new KpiBucketCollector();
		loadingMonitors.forEach(collector::addKpiBucket);
		
		dto.updateFromBucket(collector.getBuckets());
		
		return dto;
	}
	
	
	
}
