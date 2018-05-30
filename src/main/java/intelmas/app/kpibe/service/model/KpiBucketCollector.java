package intelmas.app.kpibe.service.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import intelmas.app.kpibe.model.cassandra.LoadingMonitorEntity;

public class KpiBucketCollector {

	private Map<String, KpiBucket> kpiBucketMap;
	
	public KpiBucketCollector() {
		this.kpiBucketMap = new HashMap<String, KpiBucket>();
	}
	
	public static List<KpiBucket> generateBuckets(Iterable<LoadingMonitorEntity> entities){
		KpiBucketCollector collector = new KpiBucketCollector();
		collector.addKpiBuckets(entities);
		
		return collector.getBuckets();
	}
	
	public void addKpiBuckets(Iterable<LoadingMonitorEntity> entities){
		if(entities == null) return;
		entities.forEach(this::addKpiBucket);
	}
	
	public void addKpiBucket(LoadingMonitorEntity entity){
		if(entity == null) return;
		addKpiBucket(entity.getName(), entity.getParameter(), entity.getHour(), entity.getMinute());
	}
	
	public void addKpiBucket(String name, String parameter, int hour, int minute){
		if(name  == null || parameter == null) return;
		
		KpiBucket kpiBucket = kpiBucketMap.get(name);
		if(kpiBucket == null){
			kpiBucket = new KpiBucket(name);
			kpiBucketMap.put(name, kpiBucket);
		}
		
		kpiBucket.addParameter(parameter, hour, minute);
	}
	
	public List<KpiBucket> getBuckets() {
		return new ArrayList<KpiBucket>(kpiBucketMap.values());
	}
	
}
