package intelmas.app.kpibe.controller.dto.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import intelmas.app.kpibe.constant.Aggregation;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntityKey;
import intelmas.app.kpibe.model.cassandra.Threshold;
import intelmas.app.kpibe.repository.cassandra.CounterIdByNameRepository;
import intelmas.app.kpibe.tools.Utils;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KpiFormula {

	private String id;
	private String vendor;
	private String organisation;
	private String category;
	private String name;
	private String nodeversion;
	private String userid;
	private Formula formula;
	private Aggregation aggregation;
    private Threshold critical;
    private Threshold major;
	private Threshold minor;
	private Threshold normal;
	private Boolean active;
	private String type;
	private Double max;
	private String modifiedby;
	private String modifiedat;
	
	public final static String TYPE_STANDARD = "standard";
	public final static String TYPE_CUSTOM = "custom";
	
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
	
	public String getVendor() {
		return vendor;
	}
	
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public Formula getFormula() {
		return formula;
	}
	
	public void setFormula(Formula formula) {
		this.formula = formula;
	}
	
	public Aggregation getAggregation() {
		return aggregation;
	}
	
	public void setAggregation(Aggregation aggregation) {
		this.aggregation = aggregation;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getNodeversion() {
		return nodeversion;
	}
	
	public void setNodeversion(String nodeversion) {
		this.nodeversion = nodeversion;
	}
	
	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
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
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Double getMax() {
		return max;
	}
	
	public void setMax(Double max) {
		this.max = max;
	}
	
	public String getModifiedby() {
		return modifiedby;
	}
	
	public void setModifiedby(String modifiedby) {
		this.modifiedby = modifiedby;
	}
	
	public String getModifiedat() {
		return modifiedat;
	}
	
	public void setModifiedat(String modifiedat) {
		this.modifiedat = modifiedat;
	}
	
	public void setModifiedat(Timestamp datetime) {
		Instant datetimeInstant = datetime.toInstant();
		ZoneId zoneId = Utils.TIMEZONE;
		ZonedDateTime zdt = ZonedDateTime.ofInstant( datetimeInstant , zoneId );
		
		this.modifiedat = zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZZ"));
	}
	
	public KpiFormulaEntity generateEntity(CounterIdByNameRepository counterIdByNameRepository){
		String organisation = this.getOrganisation();
		if(StringUtils.isBlank(organisation)) 
			organisation = this.getVendor();
		
		if(StringUtils.isBlank(organisation)) 
			organisation = "intelmas"; // hardcoded
		
		KpiFormulaEntityKey key = new KpiFormulaEntityKey(organisation, this.getCategory(), this.getName(), this.getNodeversion());
		if(!StringUtils.isBlank(this.getId())){
			key = new KpiFormulaEntityKey(this.getId());
		}
		
		KpiFormulaEntity entity = new KpiFormulaEntity();
		entity.setPk(key);
		
		Formula formula = this.getFormula();
		if(formula != null)
			entity.setFormula(formula.generateFormulaEntity(organisation, counterIdByNameRepository));
		
		entity.setAggregation(this.getAggregation());
		entity.setType(this.getType());
		entity.setCritical(this.getCritical());
		entity.setMajor(this.getMajor());
		entity.setMinor(this.getMinor());
		entity.setNormal(this.getNormal());
		entity.setActive(this.getActive());
		entity.setMax(this.getMax());
		entity.setUserid(this.getUserid());
		
		entity.setModifiedat(new Date());
		entity.setModifiedby(this.getModifiedby());
		return entity;
	}
}
