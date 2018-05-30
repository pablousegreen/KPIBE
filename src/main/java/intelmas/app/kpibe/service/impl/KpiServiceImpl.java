package intelmas.app.kpibe.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;

import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortMode;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import intelmas.app.kpibe.constant.Aggregation;
import intelmas.app.kpibe.constant.TermConstant;
import intelmas.app.kpibe.controller.dto.KpiDTO;
import intelmas.app.kpibe.controller.dto.KpisDTO;
import intelmas.app.kpibe.controller.dto.TypeCompleteStatusesDTO;
import intelmas.app.kpibe.controller.dto.model.Kpi;
import intelmas.app.kpibe.controller.dto.model.TypeCompleteStatus;
import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.HourlyKpiEntity;
import intelmas.app.kpibe.model.cassandra.KpiFormulaEntity;
import intelmas.app.kpibe.model.elastic.KpiDocument;
import intelmas.app.kpibe.repository.cassandra.HourlyKpiRepository;
import intelmas.app.kpibe.repository.cassandra.KpiFormulaRepository;
import intelmas.app.kpibe.repository.elastic.KpiRepository;
import intelmas.app.kpibe.service.KpiService;
import intelmas.app.kpibe.tools.Utils;

@Service
public class KpiServiceImpl implements KpiService {
	
	@Autowired
	private KpiRepository kpiRepository;
	
	// @Autowired
	private KpiFormulaRepository kpiFormulaRepository;
	
	private HourlyKpiRepository hourlyKpiRepository;
		
	final String index = "kpis";
	
	final String type = "generic";
	
	private static final Logger LOG = LoggerFactory.getLogger(KpiServiceImpl.class);
	
	
	public KpiServiceImpl(KpiRepository kpiRepository,
			KpiFormulaRepository kpiFormulaRepository,
			HourlyKpiRepository hourlyKpiRepository) {
		this.kpiRepository = kpiRepository;
		this.kpiFormulaRepository = kpiFormulaRepository;
		this.hourlyKpiRepository = hourlyKpiRepository;
	}
	
	@Override
	public TypeCompleteStatusesDTO getStatus(String organisation, Timestamp datetime) throws ProcessingException{
		final BoolQueryBuilder builder = boolQuery()
				.must(matchQuery("datetime", datetime))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));
		
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		TermsAggregationBuilder grouper = AggregationBuilders.terms("by_node_type").field("nodeType");
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.aggregation(grouper);
		searchSourceBuilder.from(0).size(1);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		
		if(searchResponse == null || searchResponse.status() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		Aggregations aggregations = searchResponse.getAggregations();
		
		Map<String, Integer> nodeTypeMap = new HashMap<String, Integer>();
		Iterable<KpiFormulaEntity> formulaEntities = kpiFormulaRepository.findAll();
		if(formulaEntities == null || !formulaEntities.iterator().hasNext())
			throw new ProcessingException("3000", "Unable to retrieve the node types");
		
		for(KpiFormulaEntity formulaEntity: formulaEntities){
			nodeTypeMap.put(formulaEntity.getCategory(), 0);
		}
		
		Terms nodeTypeTerms = aggregations.get("by_node_type");
		
		if(nodeTypeTerms == null || nodeTypeTerms.getBuckets() == null) 
			throw new ProcessingException("2000", "Unable to find nodes in the DB");
		
		for(Bucket nodeTypeBucket: nodeTypeTerms.getBuckets()){
			nodeTypeMap.put(nodeTypeBucket.getKeyAsString(), nodeTypeBucket.getDocCount() > 0? 1: 0);
		}
		
		TypeCompleteStatusesDTO dto = new TypeCompleteStatusesDTO();
		for(String nodeType: nodeTypeMap.keySet()){
			TypeCompleteStatus typeStatus = new TypeCompleteStatus(nodeType, nodeTypeMap.get(nodeType) > 0 ? "Completed": "Incomplete");
			dto.addResult(typeStatus);
		}
		
		return dto;
		
	}
	
	@Override
	public KpisDTO getSoftAlarmData(String organisation, String oss, String name, Integer resolution) throws ProcessingException {
		
		final BoolQueryBuilder builder = boolQuery()
				.must(termQuery("name", name))
				.must(termQuery("oss", oss))
				;
		
		// builder.must(rangeQuery("datetime").gt("now-1H/s"));
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));
		
		
		KpiDocument latestKpi = getLatestKpiDocument(builder);
		if(latestKpi == null) return new KpisDTO();
		
		Timestamp latestTimestamp = latestKpi.getDatetime();
		LOG.info("Get LatestTimestamp:{}", Utils.timestampToString(latestTimestamp));
		
				// .must(rangeQuery("datetime").gte("now-" + (resolution + 1) + "m/m"));
				//.must(rangeQuery("datetime").gt("now-" + (resolution + 1) + "m/m")); //+1 because sometime the resolution miss a bit
		
		if(resolution == 15){
			builder.must(
					rangeQuery("datetime")
					.gte(Utils.timestampToString(latestTimestamp))
					.lte(Utils.timestampToString(latestTimestamp))
					);
		}
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		
		searchSourceBuilder.from(0).size(5000);
		searchSourceBuilder.timeout(new TimeValue(500, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("node").order(SortOrder.ASC));
		
		
		List<KpiDocument> kpiDocuments = new ArrayList<>();
		if(resolution == 15){
			kpiDocuments = getKpiHits(searchRequest, searchSourceBuilder);
		}else if(resolution == 60){
			kpiDocuments = getKpiAggregates(organisation, latestTimestamp, oss, name);
		}
		
		KpisDTO dto = new KpisDTO();		
		dto.setFromDocument(kpiDocuments);
		dto.setTotalElements(kpiDocuments.size());
		dto.setTotalPages(1);
		return dto;
	}
	
	private List<KpiDocument> getKpiAggregates(String organisation, Timestamp latestTimestamp, String oss, String name) throws ProcessingException{
		
		LocalDateTime currentDateTime = latestTimestamp.toLocalDateTime();
		int currentMinute = currentDateTime.getMinute();
		
		if(currentMinute < 45){
			currentDateTime = currentDateTime.minusHours(1);
		}
		
		String datehour = Utils.localDateTimeToDateString(currentDateTime);
		Iterable<HourlyKpiEntity> hourlyEntities = hourlyKpiRepository.findByKey(organisation, datehour, oss, name);
		if(hourlyEntities == null) return null;
		
		return StreamSupport.stream(hourlyEntities.spliterator(), false)
			.map(KpiDocument::fromHourlyKpi)
			.collect(Collectors.toList());
		
	}
	
	private List<KpiDocument> getKpiHits(SearchRequest searchRequest, SearchSourceBuilder searchSourceBuilder) throws ProcessingException{
		
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(new TimeValue(60000));
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		
		if(searchResponse == null || searchResponse.getHits() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		List<KpiDocument> kpiDocuments = new ArrayList<>();
		
		try {
			do {
				kpiDocuments.addAll( kpiRepository.generateFromSearchResponse(searchResponse) );
			    
				SearchScrollRequest scrollRequest = new SearchScrollRequest(searchResponse.getScrollId()); 
				scrollRequest.scroll(TimeValue.timeValueSeconds(60));
			    searchResponse = kpiRepository.getClient().searchScroll(scrollRequest, new Header[0]);
			    		//.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
			} while(searchResponse.getHits().getHits().length != 0); 
		} catch(Exception e){
			throw new ProcessingException("3000", "Unable to scroll document [Exception:" + e.toString() + "]");
		}
		
		return kpiDocuments;
	}
	
	@Override
	public KpisDTO getNotificationsData(String organisation, String node, String cell, String name, Integer resolution) throws ProcessingException {
		
		
		final BoolQueryBuilder builder = boolQuery()
				.must(termQuery("name", name))
				.must(termQuery("node", node))
				.must(termQuery("cell", cell))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));
		
		KpiDocument latestKpi = getLatestKpiDocument(builder);
		if(latestKpi == null) return new KpisDTO();
		
		Timestamp latestTimestamp = latestKpi.getDatetime();	
		if(resolution == 15){
			builder.must(
					rangeQuery("datetime")
					.gte(Utils.timestampToString(latestTimestamp))
					.lte(Utils.timestampToString(latestTimestamp))
					);
		}else if(resolution == 60){
			LocalDateTime currentDateTime = latestTimestamp.toLocalDateTime();
			int currentMinute = currentDateTime.getMinute();
			
			if(currentMinute < 45){
				currentDateTime = currentDateTime.minusHours(1);
			}
			LocalDateTime startTime = currentDateTime.withMinute(0).withSecond(0).withNano(0);
			LocalDateTime endTime = currentDateTime.withMinute(45).withSecond(0).withNano(0);
			builder.must(
					rangeQuery("datetime")
					.gte(Utils.localdatetimeToString(startTime))
					.lte(Utils.localdatetimeToString(endTime))
					);
			
			LOG.info("Get notification data from {} to {}",Utils.localdatetimeToString(startTime), Utils.localdatetimeToString(endTime) );
		}
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		
		searchSourceBuilder.from(0).size(4);
		searchSourceBuilder.timeout(new TimeValue(500, TimeUnit.SECONDS));
		
		List<KpiDocument> kpiDocuments = getKpiHits(searchRequest, searchSourceBuilder);
		KpiDocument returnDoc = null;
		if(kpiDocuments != null){
			for(KpiDocument kpiDocument: kpiDocuments){
				if(returnDoc == null) returnDoc = kpiDocument;
				else returnDoc.appendValueFromKpiDocument(kpiDocument);
			}
			if(returnDoc != null) returnDoc.averagingValue(kpiDocuments.size());
		}
		
				
		KpisDTO dto = new KpisDTO();
		
		List<KpiDocument> newDocs = new ArrayList<>();
		if(returnDoc != null) newDocs.add(returnDoc);
		dto.setFromDocument(newDocs);
		dto.setTotalElements(newDocs.size());
		dto.setTotalPages(1);
		return dto;
		
		/*
		final BoolQueryBuilder builder = boolQuery()
				.must(matchQuery("name", name))
				.must(matchQuery("node", node))
				.must(matchQuery("cell", cell))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));
		
		Instant startDateInstant = Instant.now().minus(10 + resolution, ChronoUnit.MINUTES);
		Instant endDateInstant = Instant.now();
		Timestamp endDate = Timestamp.from(endDateInstant);
		Timestamp startDate = Timestamp.from(startDateInstant);
		builder.must(rangeQuery("datetime").gte( Utils.timestampToString(startDate) ).lte( Utils.timestampToString(endDate) ));
		
		if(resolution == 15){
			return getLatestKpis(builder, Boolean.FALSE, node);
		}else if(resolution == 60){
			return getLatestKpis(builder, Boolean.TRUE, node);
		}else{
			return new KpisDTO();
		}
		*/
	}
	
	
	
	
	@Override
	public KpisDTO getLatestKpisByResolution(String organisation, String node, String cell, Integer resolution) throws ProcessingException {
		
		final BoolQueryBuilder filterBuilder = boolQuery()
				.must(matchQuery("node", node))
				.must(matchQuery("cell", cell))
				.must(rangeQuery("datetime").gt("now-" + (resolution + 1) + "H/s")); //+1 because sometime the resolution miss a bit
		
		LOG.info("1A100 IN organisation: "+organisation);
		LOG.info("1A101 IN node: "+node);
		LOG.info("1A102 IN cell: "+cell);
		LOG.info("1A103 IN resolution: "+resolution);
		
		if(!StringUtils.isBlank(organisation))
			filterBuilder.must(matchQuery("organisation", organisation));
		//GO TO DATA LAYER
		KpiDocument latestKpi = getLatestKpiDocument(filterBuilder);
		if(latestKpi == null) return new KpisDTO();
		
		Timestamp latestTimestamp = latestKpi.getDatetime();
		LOG.info("1A104: "+latestTimestamp);
		
		LocalDateTime endDateTime = latestTimestamp.toLocalDateTime();
		LOG.info("1A105: "+endDateTime);
		endDateTime = endDateTime.withNano(0).withSecond(0);
		LOG.info("1A106: "+endDateTime);
		
		LocalDateTime startDateTime = endDateTime.minusHours(resolution);
		LOG.info("1A107: "+startDateTime);
		Timestamp endDate = Timestamp.valueOf(endDateTime);
		LOG.info("1A108: "+endDate);
		Timestamp startDate = Timestamp.valueOf(startDateTime);
		LOG.info("1A109: "+startDate);
		/*
		LocalDateTime endDateTime = LocalDateTime.now(Utils.TIMEZONE);
		endDateTime = endDateTime.withNano(0).withSecond(0);
		
		if(endDateTime.getMinute() > 45) endDateTime = endDateTime.withMinute(45);
		else if(endDateTime.getMinute() > 30) endDateTime = endDateTime.withMinute(30);
		else if(endDateTime.getMinute() > 15) endDateTime = endDateTime.withMinute(15);
		else endDateTime = endDateTime.withMinute(0);
		
		LocalDateTime startDateTime = endDateTime.minusHours(resolution);
		
		Timestamp endDate = Timestamp.valueOf(endDateTime);
		Timestamp startDate = Timestamp.valueOf(startDateTime);
		
		*/
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(filterBuilder);
		
		searchSourceBuilder.from(0).size(4 * resolution * 12);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.ASC));
		
		searchRequest.source(searchSourceBuilder);
		LOG.info("1A110: "+StringUtils.substring(node, 0, 3));
		searchRequest.routing(StringUtils.substring(node, 0, 3));
		
		KpisDTO dto = new KpisDTO();
		
		List<KpiDocument> kpiDocuments = kpiRepository.queryForList(searchRequest);
		Set<Kpi> completeKpis = generateCompleteKpiSets(organisation, kpiDocuments, startDate, endDate, node, cell);
		dto.setKpis(completeKpis);
		dto.setTotalElements(kpiDocuments.size());
		dto.setTotalPages(1);
		return dto;
	}
	
	
	private Set<Kpi> generateCompleteKpiSets(String organisation, List<KpiDocument> kpiDocuments, Timestamp startDate, Timestamp endDate, String node, String cell) throws ProcessingException {
		
		if(kpiDocuments == null) 
			return new LinkedHashSet<Kpi>();
		
		
		Map<String, Kpi> emptyKpis = new HashMap<String, Kpi>();
		Map<String, Kpi> kpiMapping = kpiDocuments.stream()
			.map(document -> { 
				Kpi kpi = document.generateKpi();
				if(!emptyKpis.containsKey(kpi.getName())) emptyKpis.put(kpi.getName(), generateEmptyKpi(kpi));

				return kpi;
			}).collect(Collectors.toConcurrentMap(Kpi::obtainKey, kpi->kpi, (value1, value2) -> value1));
		
		Set<Kpi> result = new LinkedHashSet<Kpi>();
		
		Instant startInstant = startDate.toInstant();
		Instant endInstant = endDate.toInstant();
		 
		Collection<Kpi> emptyKpiCollections = emptyKpis.values();
		while(!startInstant.isAfter(endInstant)){
			// Insert all empty kpi
			Timestamp templateTimestamp = Timestamp.from(startInstant);
			String timestampString = Utils.timestampToString(templateTimestamp);
			
			emptyKpiCollections.stream().map( kpi -> {
				String key = kpi.getName() + timestampString;
				Kpi realKpi = kpiMapping.get(key);
				if(realKpi == null) return new Kpi(kpi, templateTimestamp); //Return empty Kpis
				return realKpi;
			})
			.forEach(result::add);
			
			startInstant = startInstant.plus(15, ChronoUnit.MINUTES);
		}
		
		return result;
		
	}
	
	private Kpi generateEmptyKpi(Kpi templateKpi){
		Kpi kpi = new Kpi();
		kpi.setId("-1");
		kpi.setValue(Double.NaN);
		
		kpi.setRegion(templateKpi.getRegion());
		kpi.setTech(templateKpi.getTech());
		kpi.setOss(templateKpi.getOss());
		kpi.setNode(templateKpi.getNode());
		kpi.setNodeType(templateKpi.getNodeType());
		kpi.setCell( templateKpi.getCell() );
		kpi.setName(templateKpi.getName());
		kpi.setKpiType( templateKpi.getKpiType() );
		kpi.setCritical(templateKpi.getCritical());
		kpi.setMajor(templateKpi.getMajor());
		kpi.setMinor(templateKpi.getMinor());
		kpi.setNormal(templateKpi.getNormal());
		
		Map<String, Double> parameters = templateKpi.getParameters();
		if(parameters != null) {
			for(String paramName: parameters.keySet()){
				parameters.put(paramName, Double.NaN);
			}
		}
		
		kpi.setParameters(parameters);
		return kpi;
	}
	
	@Override
	public KpisDTO getKpisByDate(String organisation, Timestamp startDate, Timestamp endDate, String node, String cell) throws ProcessingException{
		
		final BoolQueryBuilder builder = boolQuery()
				.must(termQuery("node", node))
				.must(termQuery("cell", cell))
				.must(rangeQuery("datetime").gte(Utils.timestampToString(startDate)).lte(Utils.timestampToString(endDate)))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(termQuery("organisation", organisation));
		
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		
		long hourDifferences = (endDate.getTime() - startDate.getTime()) / 3600000;
		int maxSize = 100;
		if(hourDifferences < 24) maxSize = (int)hourDifferences * 4;
		
		searchSourceBuilder.from(0).size(maxSize);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.DESC));
		
		searchRequest.source(searchSourceBuilder);
		searchRequest.routing(StringUtils.substring(node, 0, 3));
		
		List<KpiDocument> kpiDocuments = kpiRepository.queryForList(searchRequest);
		
		KpisDTO dto = new KpisDTO();
		
		if(kpiDocuments == null || kpiDocuments.size() == 0) return dto;
		
		
		Set<Kpi> kpis = new LinkedHashSet<Kpi>();
		dto.setKpis(kpis);
		
		kpiDocuments.forEach( kpiDocument -> {
			kpis.add(kpiDocument.generateKpi());
		});
		
		Set<Kpi> completeKpis = generateCompleteKpiSets(organisation, kpiDocuments, startDate, endDate, node, cell);
		dto.setKpis(completeKpis);
		
		return dto;
	}
	
	
	@Override
	public KpisDTO getLatestKpis(String organisation, String node, String cell) throws ProcessingException {
		
		final BoolQueryBuilder builder = boolQuery();
		
		if(node != null)
			builder.must(matchQuery("node", node));
		
		if(cell != null)
			builder.must(matchQuery("cell", cell));
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));
		
		KpiDocument latestKpiDocument = getLatestKpiDocument(builder);
		if(latestKpiDocument == null) return new KpisDTO();
		
		builder.must(matchQuery("datetime", latestKpiDocument.getDatetimeAsString()));
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		
		searchSourceBuilder.from(0).size(64);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.DESC));
		
		searchRequest.source(searchSourceBuilder);
		searchRequest.routing(StringUtils.substring(node, 0, 3));
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		
		if(searchResponse == null || searchResponse.status() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		KpisDTO dto = new KpisDTO();
		
		SearchHits searchHits = searchResponse.getHits();
		if(searchHits == null) return dto;
		
		Set<KpiDocument> documents = new HashSet<>();
		SearchHit[] hits = searchHits.getHits();
		for(SearchHit hit: hits){
			documents.add(new KpiDocument(hit));
		}
		
		dto.setFromDocument(documents);
		return dto;
	}
	
	
	@Override
	public KpisDTO getAggregatedKpisByDateAndTerm(Timestamp startDate, Timestamp endDate, TermConstant term, String value) throws ProcessingException {
		final QueryBuilder builder = boolQuery()
				.must(termQuery(term.toString(), value))
				.must(rangeQuery("datetime").gte(Utils.timestampToString(startDate)).lte(Utils.timestampToString(endDate)))
				;
		
		return getAggregatedKpis(builder);
	}
	
	private KpisDTO getAggregatedKpis(QueryBuilder builder) throws ProcessingException {
		
		FilterAggregationBuilder grouper = AggregationBuilders.filter("filter", builder)
				.subAggregation(
						AggregationBuilders.terms("by_name").field("name").order(BucketOrder.key(false)).subAggregation(
								AggregationBuilders.terms("by_datetime").field("datetime").order(BucketOrder.key(true)).subAggregation(
										AggregationBuilders.avg("aggregatedKpi").field("value")
							)
						)
					);
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.aggregation(grouper);
		searchSourceBuilder.from(0).size(1);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		if(searchResponse == null || searchResponse.getAggregations() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		Aggregations aggregations = searchResponse.getAggregations();
		
		if(aggregations == null) 
			throw new ProcessingException("2000", "Unable to find nodes in the DB");
		
		Filter filteredAggregations = aggregations.get("filter");
		if(filteredAggregations == null || filteredAggregations.getAggregations() == null) throw new ProcessingException("4000", "Unable to get filtered aggregations");
		
		KpisDTO dto = new KpisDTO();
		Set<Kpi> kpis = new HashSet<Kpi>();
		dto.setKpis(kpis);
		
		
		Terms nameTerms = filteredAggregations.getAggregations().get("by_name");
		if(nameTerms == null || nameTerms.getBuckets() == null) return dto;
		List<? extends Bucket> nameBuckets = nameTerms.getBuckets();
		for(Bucket nameBucket: nameBuckets){
			Aggregations nameAggregations = nameBucket.getAggregations();
			if(nameAggregations == null) continue;
			
			Terms datetimeTerms = nameAggregations.get("by_datetime");
			if(datetimeTerms == null || datetimeTerms.getBuckets() == null) continue;
				
			List<? extends Bucket> datetimeBuckets = datetimeTerms.getBuckets();
			for(Bucket datetimeBucket: datetimeBuckets){
				Aggregations datetimeAggregations = datetimeBucket.getAggregations();
				if(datetimeAggregations == null) continue;
				
				Avg aggregatedKpiAggregation = datetimeAggregations.get("aggregatedKpi");
				if(aggregatedKpiAggregation == null) continue;
				
				Kpi kpi = new Kpi();
				kpi.setName(nameBucket.getKeyAsString());
				kpi.setDatetime(datetimeBucket.getKeyAsString());
				kpi.setValue(aggregatedKpiAggregation.getValue());
				
				kpis.add(kpi);
			}
		}
		
        return dto;
	}
	
	@Override
	public KpisDTO getKpisDashboardPerNode(String organisation, String node)
			throws ProcessingException {
		
		final BoolQueryBuilder builder = boolQuery()
				.must(termQuery("node", node))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));
		
		KpisDTO dto = new KpisDTO();
		dto.setTotalElements(0);
		dto.setTotalPages(0);
		
		Iterable<KpiFormulaEntity> formulas = kpiFormulaRepository.findByOrganisation(organisation);
		if(formulas == null) return dto;
		
		Map<String, KpiFormulaEntity> formulaMap = StreamSupport.stream(formulas.spliterator(), false).collect(Collectors.toMap(KpiFormulaEntity::getName, e -> e));
		
		
		KpiDocument latestKpiDocument = getLatestKpiDocument(builder);
		if(latestKpiDocument == null){
			return dto;
		}
		
		builder.must(matchQuery("datetime", latestKpiDocument.getDatetimeAsString()));
		
		NestedAggregationBuilder parameterAggregation = AggregationBuilders.nested("parameters", "parameters")
				.subAggregation(AggregationBuilders
						.terms("parameterName").field("parameters.name")
						.subAggregation(AggregationBuilders.sum("sumValue").field("parameters.value")));
		
		TermsAggregationBuilder termAggregationBuilder = AggregationBuilders.terms("node_aggregation").field("node")
				.subAggregation(
						AggregationBuilders.terms("name_aggregation").field("name")
						.subAggregation(parameterAggregation));
		
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.aggregation(termAggregationBuilder);
		searchSourceBuilder.from(0).size(0);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		if(searchResponse == null || searchResponse.getHits() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
				
		Aggregations aggregations = searchResponse.getAggregations();
		Terms terms = aggregations.get("node_aggregation");
		List<KpiDocument> kpiDocuments = terms.getBuckets().stream()
			.map(bucket ->  ((Terms)bucket.getAggregations().get("name_aggregation")).getBuckets())
			.flatMap(nameBuckets -> nameBuckets.stream())
			.map(nameBucket -> { 
				KpiFormulaEntity formulaEntity = formulaMap.get(nameBucket.getKeyAsString());
				if(formulaEntity == null) return null;
				try {
					return new KpiDocument(formulaEntity, organisation, latestKpiDocument.getDatetime(), nameBucket);
				} catch (ProcessingException e1) {
					return null;
				}})
			.filter(document -> document != null)
			.collect(Collectors.toList());
		
		dto.setTotalElements(kpiDocuments.size());
		dto.setFromDocument(kpiDocuments);
		
		return dto;
	}
	
	@Override
	public KpisDTO getKpisAggregationPerTime(String organisation, String kpiName, TermConstant term, String termValue, String resolution, Timestamp startDate,
			Timestamp endDate) throws ProcessingException {
		final BoolQueryBuilder builder = boolQuery()
				.must(termQuery("name", kpiName))
				.must(termQuery(term.toString(), termValue))
				.must(rangeQuery("datetime").gte(Utils.timestampToString(startDate)).lte(Utils.timestampToString(endDate)))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));
		
		
		Iterable<KpiFormulaEntity> formulas = kpiFormulaRepository.findByOrganisation(organisation);
		KpiFormulaEntity kpiFormula = StreamSupport.stream(formulas.spliterator(), false).filter(formula -> StringUtils.equals(kpiName, formula.getName())).findFirst().orElse(null);
		if(kpiFormula == null) throw new ProcessingException("3000", "Unable to find formula [vendor:" + organisation + "][name:" + kpiName + "]");
		
		
		AggregationBuilder valueAggregation = AggregationBuilders.avg("aggregatedValue").field("value");
		if(kpiFormula.getAggregation() == Aggregation.SUM)
			valueAggregation = AggregationBuilders.sum("aggregatedValue").field("value");
		
		NestedAggregationBuilder parameterAggregation = AggregationBuilders.nested("parameters", "parameters")
		.subAggregation(AggregationBuilders
				.terms("parameterName").field("parameters.name")
				.subAggregation(AggregationBuilders.avg("averageValue").field("parameters.value")) 
				.subAggregation(AggregationBuilders.sum("sumValue").field("parameters.value")));
		
		DateHistogramInterval interval = DateHistogramInterval.DAY;
		if(StringUtils.equals(resolution, "1D")) 
			interval = DateHistogramInterval.DAY;
		else if(StringUtils.equals(resolution, "60"))
			interval = DateHistogramInterval.minutes(60);
		else if(StringUtils.equals(resolution, "15"))
			interval = DateHistogramInterval.minutes(15);
		else
			throw new ProcessingException("2000", "Invalid resolution");
		
		DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("daily_kpi")
			.field("datetime").dateHistogramInterval(interval)
			.timeZone(DateTimeZone.forID("Mexico/General"))
				.subAggregation(valueAggregation)
				.subAggregation(parameterAggregation);
		
		
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.aggregation(dateHistogramBuilder);
		searchSourceBuilder.from(0).size(1);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.DESC).sortMode(SortMode.MIN));
		
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		if(searchResponse == null || searchResponse.getHits() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		// long totalSize = searchResponse.getHits().totalHits;
		// List<KpiDocument> kpiDocuments = kpiRepository.generateFromSearchResponse(searchResponse);
		
		Aggregations aggregations = searchResponse.getAggregations();
		Histogram histogram = aggregations.get("daily_kpi");
		List<KpiDocument> kpiDocuments = histogram.getBuckets().stream().map(bucket -> new KpiDocument(kpiFormula, bucket))
			.collect(Collectors.toList());
		
		KpisDTO dto = new KpisDTO();
		dto.setTotalElements(kpiDocuments.size());
		dto.setFromDocument(kpiDocuments);
		
		
		return dto;
	}
	
	
	@Override
	public KpisDTO getKpisPerTerm(String organisation, String kpiName, TermConstant term, String termValue, int page, int size) throws ProcessingException {
		
		final BoolQueryBuilder builder = boolQuery()
				.must(termQuery("name", kpiName))
				.must(termQuery(term.toString(), termValue))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));
		
		return getKpis(organisation, builder, page, size);
	}
	
	
	
	private KpisDTO getKpis(final String organisation, final BoolQueryBuilder builder, int page, int size) throws ProcessingException {
		// Add datetime in filter parameter
		KpiDocument latestKpiDocument = getLatestKpiDocument(builder);
		if(latestKpiDocument == null){
			KpisDTO dto = new KpisDTO();
			dto.setTotalElements(0);
			dto.setTotalPages(0);
			return dto;
		}	
		
		
		builder.must(matchQuery("datetime", latestKpiDocument.getDatetimeAsString()));
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		
		searchSourceBuilder.from(page).size(size);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.DESC).sortMode(SortMode.MIN));
		
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		if(searchResponse == null || searchResponse.getHits() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		long totalSize = searchResponse.getHits().totalHits;
		List<KpiDocument> kpiDocuments = kpiRepository.generateFromSearchResponse(searchResponse);
		
		KpisDTO dto = new KpisDTO();
		dto.setTotalElements(totalSize);
		if(size > 0) dto.setTotalPages( (totalSize / size) + 1); 
		dto.setFromDocument(kpiDocuments);
		
		
		return dto;
	}
	
	@Override
	public KpisDTO getKpisByType(String type) throws ProcessingException {
		final BoolQueryBuilder builder = boolQuery()
				//.must(termQuery("nodeType", type))
				.filter(termQuery("nodeType", type))
				;
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.from(0).size(100);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.DESC).sortMode(SortMode.MIN));
		
		searchRequest.source(searchSourceBuilder);
		
		Iterable<KpiDocument> documents = kpiRepository.queryForList(searchRequest);
		
		KpisDTO dto = new KpisDTO();
		dto.setFromDocument(documents);
		
        return dto;
	}


	@Override
	public KpiDTO getAverageKpi(String organisation, String kpiName, TermConstant term, String termValue) throws ProcessingException {
		
		final BoolQueryBuilder builder = boolQuery()
				.must( termQuery("name", kpiName))
				.must( termQuery(term.toString(), termValue))
				;
		
		
		if(!StringUtils.isBlank(organisation))
			builder.must(termQuery("organisation", organisation));
		
		FilterAggregationBuilder filterBuilder = AggregationBuilders.filter("termfilter", builder)
				.subAggregation(AggregationBuilders.avg("averageValue").field("value"));
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.aggregation(filterBuilder);
		searchSourceBuilder.from(0).size(1);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.DESC).sortMode(SortMode.MIN));
		
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		if(searchResponse == null || searchResponse.getHits() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		Aggregations aggregations = searchResponse.getAggregations();
		
		Filter filter = aggregations.get("termfilter");
		if(filter == null || filter.getAggregations() == null) throw new ProcessingException("4000", "Unable to get filtered response from elasticsearch");
		
		Avg averageKpiAggregation = filter.getAggregations().get("averageValue");
		if(averageKpiAggregation == null) throw new ProcessingException("4000", "Unable to get average KPI value");
		double aggregatedKpiValue = averageKpiAggregation.getValue();
		
		KpiDTO dto = new KpiDTO();
		Kpi kpi = new Kpi();
		SearchHit[] searchHit = searchResponse.getHits().getHits();
		if(searchHit == null || searchHit.length == 0) return dto;
		
		KpiDocument document = new KpiDocument(searchHit[0]);
		kpi = document.generateKpi();
		kpi.setRegion(null);
		kpi.setNode(null);
		kpi.setCell(null);
		kpi.setTech(null);
		
		if(term == TermConstant.node)
			kpi.setNode(termValue);
		else if(term == TermConstant.region)
			kpi.setRegion(termValue);
		else if(term == TermConstant.tech)
			kpi.setTech(termValue);
		kpi.setValue(aggregatedKpiValue);
		
		dto.setKpi(kpi);
		
		return dto;
	}
	

	
	private KpiDocument getLatestKpiDocument(final BoolQueryBuilder builder) throws ProcessingException{
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		
		searchSourceBuilder.from(0).size(1);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.DESC));
		
		searchRequest.source(searchSourceBuilder);
		
		//SEARCH DATA IN DB VIA JPA REPOSITORY
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		if(searchResponse == null || searchResponse.getHits() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		List<KpiDocument> kpiDocuments = kpiRepository.generateFromSearchHits(searchResponse.getHits());
		for (KpiDocument  kpi: kpiDocuments){
			LOG.info("kpi.Date: "+kpi.getDatetime()+ "_ getDatetimeAsString: " +kpi.getDatetimeAsString());
		}
		if(kpiDocuments == null || kpiDocuments.size() == 0) return null;
		
		return kpiDocuments.get(0);
	}
	
	@Override
	public KpisDTO getTopHitKpis(String organisation, Timestamp startDate, Timestamp endDate, String kpiName, 
			TermConstant term, String termValue, Integer size, SortOrder order) throws ProcessingException {
		
		final BoolQueryBuilder builder = boolQuery()
				.must(matchQuery("name", kpiName))
				.must(rangeQuery("datetime").gte(Utils.timestampToString(startDate)).lte(Utils.timestampToString(endDate)))
				;
		
		if(!StringUtils.equals(termValue, "null"))  {
			switch(term) {
				case region:
					builder.must(matchQuery("region", termValue));
					break;
				case tech:
					builder.must(matchQuery("tech", termValue));
					break;
				default:
					break;
			}
		}
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));
		
		return getTopHitKpis(builder, kpiName, size, order);
	}
	

	private KpisDTO getTopHitKpis(final QueryBuilder builder, String name, Integer size, SortOrder sortOrder) throws ProcessingException {
		
		Boolean isAscending = Boolean.TRUE;
		if(sortOrder == SortOrder.DESC)
			isAscending = Boolean.FALSE;
		
		AvgAggregationBuilder avgBuilder = AggregationBuilders.avg("aggregatedKpi").field("value");
		TermsAggregationBuilder termsBuilder = AggregationBuilders.terms("by_node").field("node").size(size)
				.order(BucketOrder.aggregation("aggregatedKpi", isAscending)).subAggregation(avgBuilder);
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.aggregation(termsBuilder);
		searchSourceBuilder.from(0).size(1);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.fetchSource(false);
		
		
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		if(searchResponse == null || searchResponse.getAggregations() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		Aggregations aggregations = searchResponse.getAggregations();
		Terms nodeTerms = aggregations.get("by_node");
		
		if(nodeTerms == null || nodeTerms.getBuckets() == null) 
			throw new ProcessingException("2000", "Unable to find nodes in the DB");
		
		Set<Kpi> kpis = new LinkedHashSet<Kpi>();
		
		for(Bucket nodeBucket: nodeTerms.getBuckets()){
			Aggregations nodeAggregations = nodeBucket.getAggregations();
			Avg aggregatedKpiAggregation = nodeAggregations.get("aggregatedKpi");
			
			Kpi kpi = new Kpi();
			kpi.setNode(nodeBucket.getKeyAsString());
			kpi.setName(name);
			kpi.setValue(aggregatedKpiAggregation.getValue());
			kpis.add(kpi);
		}
			
		KpisDTO dto = new KpisDTO();
		dto.setKpis(kpis);
		dto.setTotalElements(nodeTerms.getBuckets().size());
		
		if(size == 0) return dto;
		dto.setTotalPages(nodeTerms.getBuckets().size() / size);
        return dto;
	}

	
	@Override
	public KpisDTO getLatestHourKpis(String organisation, String node, String cell) throws ProcessingException {
		
		final BoolQueryBuilder builder = boolQuery()
				.must(termQuery("node", node))
				.must(termQuery("cell", cell))
				.must(rangeQuery("datetime").gte("now-1H/m"))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(termQuery("organisation", organisation));
		
		return getLatestHourKpis(builder, node);
	}

	@Override
	public KpisDTO getLatestHourKpisByTerm(String organisation, TermConstant term, String value) throws ProcessingException {
		
		final BoolQueryBuilder builder = boolQuery()
				.must(termQuery(term.toString(), value))
				.must(rangeQuery("datetime").gte("now-1H/m"))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(termQuery("organisation", organisation));
		
		return getLatestHourKpis(builder, null);
	}
	
	private KpisDTO getLatestHourKpis(QueryBuilder builder, String node) throws ProcessingException {
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.from(0).size(90);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.ASC).sortMode(SortMode.MIN));
		
		searchRequest.source(searchSourceBuilder);
		if(node != null) searchRequest.routing(StringUtils.substring(node, 0, 3));
		
		KpisDTO dto = new KpisDTO();
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		if(searchResponse == null || searchResponse.getHits() == null) return dto;
		
		dto.setTotalElements(searchResponse.getHits().getTotalHits());
		List<KpiDocument> kpiDocuments = kpiRepository.generateFromSearchResponse(searchResponse);
		Set<Kpi> kpis = new LinkedHashSet<Kpi>();
		dto.setKpis(kpis);
		
		if(kpiDocuments == null) return dto;
		
		kpiDocuments.forEach( kpiDocument -> {
			kpis.add(kpiDocument.generateKpi());
		});
		
		return dto;
	}
	
	/*
	private KpisDTO getLatestKpis(final QueryBuilder builder, Boolean calculateAverage, String node) throws ProcessingException {
		
		TopHitsAggregationBuilder topHitsBuilder = AggregationBuilders.topHits("latestKpi")
				.fetchSource(true)
				.sort("datetime", SortOrder.DESC).size(96)
				;
		
		TermsAggregationBuilder nameGrouper = AggregationBuilders.terms("by_name").field("name");
		nameGrouper.subAggregation(topHitsBuilder);
		
		if(calculateAverage){
			nameGrouper.subAggregation(AggregationBuilders.avg("aggregatedKpi").field("value"));
			
			nameGrouper.subAggregation(AggregationBuilders.nested("parameters", "parameters")
					.subAggregation(AggregationBuilders
							.terms("parameterName").field("parameters.name")
							.subAggregation(AggregationBuilders.avg("parameterValue").field("parameters.value"))  ));
		}
		
		TermsAggregationBuilder grouper = AggregationBuilders.terms("by_node").field("node")
			.subAggregation(
				AggregationBuilders.terms("by_cell").field("cell")
			 		.subAggregation(nameGrouper)
			    );
		
		
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.types(type);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(builder);
		searchSourceBuilder.aggregation(grouper);
		searchSourceBuilder.from(0).size(96);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchSourceBuilder.sort(new FieldSortBuilder("datetime").order(SortOrder.ASC));
		
		searchRequest.source(searchSourceBuilder);
		if(node != null) searchRequest.routing(StringUtils.substring(node, 0, 3));
		
		SearchResponse searchResponse = kpiRepository.search(searchRequest);
		if(searchResponse == null || searchResponse.getAggregations() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		Aggregations aggregations = searchResponse.getAggregations();
		
		if(aggregations == null) 
			throw new ProcessingException("2000", "Unable to find nodes in the DB");

		
		KpisDTO dto = new KpisDTO();
		dto.setKpis(new HashSet<>());
		
		Terms nodeTerms = aggregations.get("by_node");
		if(nodeTerms == null || nodeTerms.getBuckets() == null) return dto;
		
		for(Bucket nodeBucket: nodeTerms.getBuckets()){
			Terms cellTerms = nodeBucket.getAggregations().get("by_cell");
			if(cellTerms == null || cellTerms.getBuckets() == null) continue;
			
			for(Bucket cellBucket: cellTerms.getBuckets()) {
				Terms nameTerms = cellBucket.getAggregations().get("by_name");
				if(nameTerms == null || nameTerms.getBuckets() == null) continue;
				
				for(Bucket nameBucket: nameTerms.getBuckets()){
					TopHits latestKpis = nameBucket.getAggregations().get("latestKpi");
					if(latestKpis == null || latestKpis.getHits() == null) continue;
					
					if(calculateAverage){
						Avg averageKpi = nameBucket.getAggregations().get("aggregatedKpi");
						
						List<KpiDocument> documents = kpiRepository.generateFromSearchHits(latestKpis.getHits());
						if(documents!= null){
							Set<Kpi> kpis = documents.stream().map(document -> {
								Kpi kpi = document.generateKpi();
								//kpi.setParameters(parameterValues);
								kpi.setValue(averageKpi.getValue());
								return kpi;
							}).collect(Collectors.toSet());
							
							dto.setKpis(kpis);
						}
					}
					else {
						List<KpiDocument> documents = kpiRepository.generateFromSearchHits(latestKpis.getHits());
						dto.setFromDocument(documents);
					}
				}
			}
		}	
		
		return dto;
	}
	*/
	
}
