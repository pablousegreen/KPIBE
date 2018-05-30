package intelmas.app.kpibe.controller.dto.model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import intelmas.app.kpibe.model.cassandra.Threshold;
import intelmas.app.kpibe.model.elastic.KpiParameter;
import intelmas.app.kpibe.tools.Utils;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Kpi {

	private String id;
	private String organisation;
	private String region;
	private String tech;
	private String oss;
	private String node;
	private String nodeType;
	private String cell;
	private String datetime;
	private String name;
	private Double value;
	private String kpiType;
	private Threshold critical;
	private Threshold major;
	private Threshold minor;
	private Threshold normal;
	
	private Map<String, Double> parameters;
	
	public Kpi() {
	}
	
	public Kpi(Kpi kpi, Timestamp datetime) {
		this.setId(kpi.getId());
		this.setRegion(kpi.getRegion());
		this.setTech(kpi.getTech());
		this.setOss(kpi.getOss());
		this.setNode(kpi.getNode());
		this.setNodeType(kpi.getNodeType());
		this.setCell( kpi.getCell() );
		this.setDatetimeTimestamp(datetime);
		this.setName(kpi.getName());
		this.setValue(kpi.getValue());
		this.setKpiType( kpi.getKpiType() );
		this.setCritical(kpi.getCritical());
		this.setMajor(kpi.getMajor());
		this.setMinor(kpi.getMinor());
		this.setNormal(kpi.getNormal());
		this.setParameters(kpi.getParameters());
	}
	
	public String obtainKey(){
		return this.getName() + this.getDatetime();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOrganisation() {
		return organisation;
	}
	
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	
	public String getRegion() {
		return region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getTech() {
		return tech;
	}
	
	public void setTech(String tech) {
		this.tech = tech;
	}
	
	public String getOss() {
		return oss;
	}

	public void setOss(String oss) {
		this.oss = oss;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}
	
	public String getDatetime() {
		return datetime;
	}
	
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
	public void setDatetimeTimestamp(Timestamp datetime) {
		if(datetime == null) return;
		this.datetime = Utils.timestampToString(datetime);
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	public String getKpiType() {
		return kpiType;
	}
	
	public void setKpiType(String kpiType) {
		this.kpiType = kpiType;
	}

	public Map<String, Double> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Double> parameters) {
		this.parameters = parameters;
	}
	
	public void updateParameters(KpiParameter[] kpiParameters) {
		if(kpiParameters == null || kpiParameters.length == 0) return;
		
		this.parameters = new HashMap<String, Double>();
		
		for(KpiParameter kpiParameter: kpiParameters){
			if(StringUtils.isBlank(kpiParameter.getName())) continue;
			this.parameters.put(kpiParameter.getName(), kpiParameter.getValue());
		}
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Threshold getCritical() {
		return critical;
	}
	
	public void setCritical(Threshold critical) {
		this.critical = critical;
	}

	public Threshold getMajor() {
		return major;
	}

	public void setMajor(Threshold major) {
		this.major = major;
	}

	public Threshold getMinor() {
		return minor;
	}

	public void setMinor(Threshold minor) {
		this.minor = minor;
	}

	public Threshold getNormal() {
		return normal;
	}

	public void setNormal(Threshold normal) {
		this.normal = normal;
	}
}
