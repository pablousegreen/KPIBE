package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import intelmas.app.kpibe.constant.Aggregation;
import intelmas.app.kpibe.controller.dto.model.KpiFormula;

@Table("kpi_formulas_mappings")
public class KpiFormulaEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325328907318698346L;

	@PrimaryKey
	private KpiFormulaEntityKey pk;
	
	private String userid;
	
	private FormulaEntity formula;
	
	private Aggregation aggregation;
	
	private Threshold critical;
	
	private Threshold major;
	
	private Threshold minor;
	
	private Threshold normal;
	
	private Boolean active;
	
	private String type;
	
	private Double max;
	
	private String modifiedby;
	
	private Date modifiedat;
	
	public KpiFormulaEntity() {
		this.pk = new KpiFormulaEntityKey();
	}
	
	public void setPk(KpiFormulaEntityKey pk) {
		this.pk = pk;
	}
	
	public String getOrganisation() {
		return this.pk.getOrganisation();
	}
	
	public String getCategory() {
		return this.pk.getCategory();
	}
	
	public String getName() {
		return this.pk.getName();
	}
	
	public String getNodeversion() {
		return this.pk.getNodeversion();
	}
	
	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public Aggregation getAggregation() {
		return aggregation;
	}
	
	public void setAggregation(Aggregation aggregation) {
		this.aggregation = aggregation;
	}
	
	public FormulaEntity getFormula() {
		return formula;
	}
	
	public void setFormula(FormulaEntity formula) {
		this.formula = formula;
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
	
	public Boolean isActive() {
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

	public Date getModifiedat() {
		return modifiedat;
	}

	public void setModifiedat(Date modifiedat) {
		this.modifiedat = modifiedat;
	}
	
	public boolean containParameterCode(String parameterCode){
		if(StringUtils.isBlank(parameterCode)) return Boolean.FALSE;
		if(this.formula == null) return Boolean.FALSE;
		
		Map<String, String> parameters = this.formula.getParametermapping();
		if(parameters == null || parameters.size() == 0) return Boolean.FALSE;
		
		return parameters.containsKey(parameterCode);
	}
	
	public KpiFormula generateKpiFormula() {
		KpiFormula kpiFormula = new KpiFormula();
		String id = this.pk.generateId();
		kpiFormula.setId(id);
		kpiFormula.setCategory(this.getCategory());
		kpiFormula.setName(this.getName());
		kpiFormula.setNodeversion(this.getNodeversion());
		kpiFormula.setUserid(this.getUserid());
		
		if(this.getFormula() != null)
			kpiFormula.setFormula(this.getFormula().toModel());
		kpiFormula.setAggregation(this.getAggregation());
		kpiFormula.setType(this.getType());
		kpiFormula.setCritical(this.getCritical());
		
		kpiFormula.setMajor(this.getMajor());
		kpiFormula.setMinor(this.getMinor());
		kpiFormula.setNormal(this.getNormal());
		kpiFormula.setActive(this.getActive());
		kpiFormula.setMax(this.getMax());
		if(this.getModifiedat() == null)
			kpiFormula.setModifiedat(Timestamp.from(Instant.now()));
		else
			kpiFormula.setModifiedat(Timestamp.from(this.getModifiedat().toInstant()));
		
		kpiFormula.setModifiedby(this.getModifiedby());
		return kpiFormula;
	}
}
