package intelmas.app.kpibe.constant;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum Aggregation {
	SUM("SUM"), AVERAGE("AVERAGE");
	
	private String name;
	private static final Map<String,Aggregation> ENUM_MAP;
	
	static {
	    Map<String,Aggregation> map = new ConcurrentHashMap<String,Aggregation>();
	    for (Aggregation instance : Aggregation.values()) {
	      map.put(instance.getName(),instance);
	    }
	    ENUM_MAP = Collections.unmodifiableMap(map);
	  }
	
	private Aggregation(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public static Aggregation fromString(String name){
		if(name == null || ENUM_MAP.get(name) == null) return Aggregation.AVERAGE;
		return ENUM_MAP.get(name);
	}
}
