package intelmas.app.kpibe.model.elastic;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import intelmas.app.kpibe.constant.Aggregation;
import intelmas.app.kpibe.controller.dto.model.Kpi;
import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.HourlyKpiEntity;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;
import intelmas.app.kpibe.model.cassandra.NodeTopologyEntity;
import intelmas.app.kpibe.model.cassandra.Threshold;
import intelmas.app.kpibe.tools.KpiFormulaCalculator;
import intelmas.app.kpibe.tools.Utils;

//@Document( indexName = "kpis" , type = "generic")
public class KpiDocument {

	@Id
    private String id;
	private String organisation;
	private String region;
	private String oss;
	private String node;
	private String tech;
	private String nodeType;
	private String nodeVersion;
	private String cell;
	
	private String kpiType;
	
	
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
	private Timestamp datetime;
	
	
	private String name;
	private Double value;
	
	private KpiParameter[] parameters;
	
	private Boolean isStandard;
	
	private String criticalType;
	private Double criticalValue;
	private String majorType;
	private Double majorValue;
	private String minorType;
	private Double minorValue;
	private String normalType;
	private Double normalValue;
	
	private DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ");
	private DateTimeFormatter bucketDateFormatterTimezone = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS[XXX][X]");
	
	
	public KpiDocument(){}
	
	public KpiDocument(String organisation, String name, Bucket bucket){
		this.setId(RandomStringUtils.randomAlphanumeric(10));
		this.setOrganisation(organisation);
		this.setName(name);
		
		Avg avg = bucket.getAggregations().get("aggregatedValue");
		this.setValue(avg.getValue());
		
		Timestamp datetime = null;
		try{
			String localtimestamp = bucket.getKeyAsString();
			localtimestamp = StringUtils.replace(localtimestamp, "T", " ");
			ZonedDateTime zonedDateTime = ZonedDateTime.parse(localtimestamp, bucketDateFormatterTimezone);
			
			datetime = Timestamp.from(zonedDateTime.toInstant());
			this.setDatetime(datetime);
		}catch(Exception e){}
		
		Nested parameters = bucket.getAggregations().get("parameters");
		Terms terms = parameters.getAggregations().get("parameterName");
		
		KpiParameter[] kpiParameters = terms.getBuckets().stream().map( term -> {
			Avg parameterAvg = term.getAggregations().get("averageValue");
			Double parameterValue = parameterAvg.getValue();
			
			return new KpiParameter(term.getKeyAsString(), parameterValue);
		})
		.toArray(KpiParameter[]::new);
		
		this.setParameters(kpiParameters);
	}
	
	public KpiDocument(KpiFormulaEntity kpiFormula, Bucket bucket){
		this.setId(RandomStringUtils.randomAlphanumeric(10));
		this.setOrganisation(kpiFormula.getOrganisation());
		this.setName(kpiFormula.getName());
		
		Double value = 0.0;
		if(kpiFormula.getAggregation() == Aggregation.SUM){
			Sum sum = bucket.getAggregations().get("aggregatedValue");
			value = sum.getValue();
		}else{
			Avg avg = bucket.getAggregations().get("aggregatedValue");
			value = avg.getValue();
		}
		
		this.setValue(value);
		
		Timestamp datetime = null;
		try{	
			
			String localtimestamp = bucket.getKeyAsString();
			localtimestamp = StringUtils.replace(localtimestamp, "T", " ");
			ZonedDateTime zonedDateTime = ZonedDateTime.parse(localtimestamp, bucketDateFormatterTimezone);
			
			datetime = Timestamp.from(zonedDateTime.toInstant());
			this.setDatetime(datetime);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		Nested parameters = bucket.getAggregations().get("parameters");
		Terms terms = parameters.getAggregations().get("parameterName");
		
		Map<String, String> aggregationMapping = kpiFormula.getFormula().getAggregationmapping();
		
		KpiParameter[] kpiParameters = terms.getBuckets().stream().map( term -> {
			String parameterAggregation = aggregationMapping.get(term.getKeyAsString());
			Aggregation paramAggregationType = Aggregation.fromString(parameterAggregation);
			
			Double parameterValue = 0.0;
			if(paramAggregationType == Aggregation.SUM){
				Sum parameterSum = term.getAggregations().get("sumValue");
				parameterValue = parameterSum.getValue();
			}else {
				Avg parameterAvg = term.getAggregations().get("averageValue");
				parameterValue = parameterAvg.getValue();
			}
			
			return new KpiParameter(term.getKeyAsString(), parameterValue);
		})
		.toArray(KpiParameter[]::new);
		
		
		this.setParameters(kpiParameters);
		if(kpiFormula.getCritical() != null){
			this.setCriticalType(kpiFormula.getCritical().getType());
			this.setCriticalValue(kpiFormula.getCritical().getValue());
		}
		
		if(kpiFormula.getMajor() != null){
			this.setMajorType(kpiFormula.getMajor().getType());
			this.setMajorValue(kpiFormula.getMajor().getValue());
		}
		
		if(kpiFormula.getMinor() != null) {
			this.setMinorType(kpiFormula.getMinor().getType());
			this.setMinorValue(kpiFormula.getMinor().getValue());
		}
		
		if(kpiFormula.getNormal() != null){
			this.setNormalType(kpiFormula.getNormal().getType());
			this.setNormalValue(kpiFormula.getNormal().getValue());
		}
		
	}
	
	public KpiDocument(KpiFormulaEntity kpiFormula, String organisation, Timestamp timestamp, 
			org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket nameBucket) throws ProcessingException{
		this.setId(RandomStringUtils.randomAlphanumeric(10));
		this.setOrganisation(organisation);
		this.setName(nameBucket.getKeyAsString());
		this.setDatetime(timestamp);
	
		Nested parameters = nameBucket.getAggregations().get("parameters");
		Terms terms = parameters.getAggregations().get("parameterName");
		
		
		KpiFormulaCalculator calculator = new KpiFormulaCalculator(kpiFormula);
		
		KpiParameter[] kpiParameters = terms.getBuckets().stream().map( term -> {
			Sum parameterSum = term.getAggregations().get("sumValue");
			Double parameterValue = parameterSum.getValue();
			calculator.setParameter(term.getKeyAsString(), parameterValue);
			return new KpiParameter(term.getKeyAsString(), parameterValue);
		})
		.toArray(KpiParameter[]::new);
		
		this.setValue(calculator.calculate());
		
		this.setParameters(kpiParameters);
		
		if(kpiFormula.getCritical() != null){
			this.setCriticalType(kpiFormula.getCritical().getType());
			this.setCriticalValue(kpiFormula.getCritical().getValue());
		}
		
		if(kpiFormula.getMajor() != null){
			this.setMajorType(kpiFormula.getMajor().getType());
			this.setMajorValue(kpiFormula.getMajor().getValue());
		}
		
		if(kpiFormula.getMinor() != null) {
			this.setMinorType(kpiFormula.getMinor().getType());
			this.setMinorValue(kpiFormula.getMinor().getValue());
		}
		
		if(kpiFormula.getNormal() != null){
			this.setNormalType(kpiFormula.getNormal().getType());
			this.setNormalValue(kpiFormula.getNormal().getValue());
		}
		
	}
	
	public KpiDocument(SearchHit searchHit){
		if(searchHit == null) return;
		Map<String, Object> sourceMap = searchHit.getSourceAsMap();
		if(sourceMap == null) return;
		
		Double value = Double.NaN;
		try{  value = (Double)sourceMap.get("value"); 	}catch(Exception e){}
		
		String organisation = "";
		try{	organisation = (String)sourceMap.get("organisation"); 	}catch(Exception e){}
		
		String node = "";
		try{    node = (String)sourceMap.get("node"); 	}catch(Exception e){}
		
		String cell = "";
		try{	cell = (String)sourceMap.get("cell"); 	}catch(Exception e){}
		
		String name = "";
		try{	name = (String)sourceMap.get("name"); 	}catch(Exception e){}
		
		String region = "";
		try{	region = (String)sourceMap.get("region"); 	}catch(Exception e){}
		
		String tech = "";
		try{	tech = (String)sourceMap.get("tech"); 	}catch(Exception e){}
		
		
		List<KpiParameter> kpiParameters = new ArrayList<KpiParameter>();
		try{	
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> listKpiParameters = (List<Map<String, Object>>)sourceMap.get("parameters");
			for(Map<String, Object> kpiParameter: listKpiParameters){
				kpiParameters.add(new KpiParameter(kpiParameter.get("name"), kpiParameter.get("value")));
			}
		}catch(Exception e){
		}
		
		Timestamp datetime = null;
		try{	
			
			String localtimestamp = (String)sourceMap.get("datetime");
			localtimestamp = StringUtils.replace(localtimestamp, "T", " ");
			localtimestamp = StringUtils.replace(localtimestamp, ".000", "");
			ZonedDateTime zonedDateTime = ZonedDateTime.parse(localtimestamp, dbDateFormatter);
			
			datetime = Timestamp.from(zonedDateTime.toInstant());
			
		}catch(Exception e){
		}
		
		
		this.setId(searchHit.getId());
		this.setOrganisation(organisation);
		this.setNode(node);
		this.setCell(cell);
		this.setRegion(region);
		this.setTech(tech);
		
		this.setValue(value);
		this.setDatetime(datetime);
		
		try{	
			String criticalType = (String)sourceMap.get("criticalType"); 
			Double criticalValue = (Double)sourceMap.get("criticalValue"); 
			this.setCriticalType(criticalType);
			this.setCriticalValue(criticalValue);
		}catch(Exception e){}
		
		try{	
			String majorType = (String)sourceMap.get("majorType"); 
			Double majorValue = (Double)sourceMap.get("majorValue"); 
			this.setMajorType(majorType);
			this.setMajorValue(majorValue);
		}catch(Exception e){}
		
		try{	
			String minorType = (String)sourceMap.get("minorType"); 
			Double minorValue = (Double)sourceMap.get("minorValue"); 
			this.setMinorType(minorType);
			this.setMinorValue(minorValue);
		}catch(Exception e){}
		
		try{	
			String normalType = (String)sourceMap.get("normalType"); 
			Double normalValue = (Double)sourceMap.get("normalValue");
			this.setNormalType(normalType);
			this.setNormalValue(normalValue);
		}catch(Exception e){}

		KpiParameter[] sourceKpiParameters = kpiParameters.toArray(new KpiParameter[kpiParameters.size()]);
		if(kpiParameters != null) this.setParameters(sourceKpiParameters);

		this.setName(name);
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

	public Timestamp getDatetime() {
		return datetime;
	}
	
	public String getDatetimeAsString(){
		return Utils.timestampToString(this.datetime);
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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

	
	public String getNodeVersion() {
		return nodeVersion;
	}

	public void setNodeVersion(String nodeVersion) {
		this.nodeVersion = nodeVersion;
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
	
	public KpiParameter[] getParameters() {
		return parameters;
	}
	
	public void setParameters(KpiParameter[] parameters) {
		this.parameters = parameters;
	}
	
	public Boolean getIsStandard() {
		return isStandard;
	}
	
	public void setIsStandard(Boolean isStandard) {
		this.isStandard = isStandard;
	}
	
	public String getCriticalType() {
		return criticalType;
	}

	public void setCriticalType(String criticalType) {
		this.criticalType = criticalType;
	}

	public Double getCriticalValue() {
		return criticalValue;
	}

	public void setCriticalValue(Double criticalValue) {
		this.criticalValue = criticalValue;
	}

	public String getMajorType() {
		return majorType;
	}

	public void setMajorType(String majorType) {
		this.majorType = majorType;
	}

	public Double getMajorValue() {
		return majorValue;
	}

	public void setMajorValue(Double majorValue) {
		this.majorValue = majorValue;
	}

	public String getMinorType() {
		return minorType;
	}

	public void setMinorType(String minorType) {
		this.minorType = minorType;
	}

	public Double getMinorValue() {
		return minorValue;
	}

	public void setMinorValue(Double minorValue) {
		this.minorValue = minorValue;
	}

	public String getNormalType() {
		return normalType;
	}

	public void setNormalType(String normalType) {
		this.normalType = normalType;
	}

	public Double getNormalValue() {
		return normalValue;
	}

	public void setNormalValue(Double normalValue) {
		this.normalValue = normalValue;
	}

	public void updateFromNodeTopology(NodeTopologyEntity entity){
		if(entity == null) return;
		
		this.setOss(entity.getOss());
		this.setRegion(entity.getRegion());
		this.setTech(entity.getTech());
		this.setNodeVersion(entity.getNodeVersion());
	}
	
	public Kpi generateKpi(){
		Kpi kpi = new Kpi();
		
		kpi.setId(this.getId());
		kpi.setOrganisation(this.getOrganisation());
		kpi.setRegion(this.getRegion());
		kpi.setTech(this.getTech());
		
		kpi.setOss(this.getOss());
		kpi.setNode(this.getNode());
		kpi.setNodeType(this.getNodeType());
		kpi.setCell(this.getCell());
		
		kpi.setDatetimeTimestamp(this.getDatetime());
		kpi.setName(this.getName());
		kpi.setValue(this.getValue());
		
		kpi.setKpiType(this.getKpiType());
		//kpi.setParameters(this.getKpi().decodeParameterValue());
		
		kpi.updateParameters(this.getParameters());
		if(this.getCriticalType() != null) kpi.setCritical(new Threshold(this.getCriticalType(), this.getCriticalValue()));
		if(this.getMajorType() != null) kpi.setMajor(new Threshold(this.getMajorType(), this.getMajorValue()));
		if(this.getMinorType() != null) kpi.setMinor(new Threshold(this.getMinorType(), this.getMinorValue()));
		if(this.getNormalType() != null) kpi.setNormal(new Threshold(this.getNormalType(), this.getNormalValue()));
		
		return kpi;
	}
	
	public void appendValueFromKpiDocument(KpiDocument kpiDocument){
		this.setValue(this.getValue() + kpiDocument.getValue());
		KpiParameter[] parameters = this.getParameters();
		KpiParameter[] addedParameters = kpiDocument.getParameters();
		if(addedParameters != null){
			Map<String, Double> addParameterMaps = Arrays.stream(addedParameters).collect(Collectors.toMap(e -> e.getName(), e-> e.getValue()));
			if(parameters != null){
				Arrays.stream(parameters)
					.forEach(parameter -> parameter.setValue(parameter.getValue() + addParameterMaps.get(parameter.getName())));
			}
		}
		
		this.setParameters(parameters);
	}
	
	public void averagingValue(int size){
		this.setValue(this.getValue() / size);
		KpiParameter[] parameters = this.getParameters();
		
		if(parameters != null){
			Arrays.stream(parameters)
				.forEach(parameter -> parameter.setValue(parameter.getValue() / size) );
		}
		
		this.setParameters(parameters);
	}
	
	public static KpiDocument fromHourlyKpi(HourlyKpiEntity hourlyEntity){
		KpiDocument kpiDocument = new KpiDocument();
		kpiDocument.setId(RandomStringUtils.randomAlphanumeric(10));
		kpiDocument.setOrganisation(hourlyEntity.getOrganisation());
		kpiDocument.setDatetime(
				Utils.hourlyKpiDateStringToTimestamp(hourlyEntity.getDatehour()));
		kpiDocument.setOss(hourlyEntity.getOss());
		kpiDocument.setNode(hourlyEntity.getNode());
		
		kpiDocument.setCell(hourlyEntity.getMoid());
		kpiDocument.setValue(hourlyEntity.getValue());
		
		Map<String,Double> hourlyParameters = hourlyEntity.getParameters();
		if(hourlyParameters != null){
			kpiDocument.setParameters(
				hourlyParameters.keySet().stream()
					.map(key -> new KpiParameter(key, hourlyParameters.get(key)))
					.toArray(KpiParameter[]::new)
				);
				
		}
		
		return kpiDocument;
	}
	
	/*
	 DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(ISO_LOCAL_DATE)
        .optionalStart()           // time made optional
        .appendLiteral('T')
        .append(ISO_LOCAL_TIME)
        .optionalStart()           // zone and offset made optional
        .appendOffsetId()
        .optionalStart()
        .appendLiteral('[')
        .parseCaseSensitive()
        .appendZoneRegionId()
        .appendLiteral(']')
        .optionalEnd()
        .optionalEnd()
        .optionalEnd()
        .toFormatter();

TemporalAccessor temporalAccessor = formatter.parseBest(value, ZonedDateTime::from, LocalDateTime::from, LocalDate::from);
if (temporalAccessor instanceof ZonedDateTime) {
    return ((ZonedDateTime) temporalAccessor);
}
if (temporalAccessor instanceof LocalDateTime) {
    return ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault());
}
return ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault());
	 */
}
