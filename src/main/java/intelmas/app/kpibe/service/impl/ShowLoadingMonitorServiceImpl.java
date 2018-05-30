package intelmas.app.kpibe.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import intelmas.app.kpibe.controller.dto.LoadingStatusDTO;
import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.cassandra.LoadingMonitorEntity;
import intelmas.app.kpibe.repository.cassandra.LoadingMonitorRepository;
import intelmas.app.kpibe.service.ShowLoadingMonitorService;
import intelmas.app.kpibe.service.model.KpiBucket;
import intelmas.app.kpibe.service.model.KpiBucketCollector;

@Service
public class ShowLoadingMonitorServiceImpl implements ShowLoadingMonitorService {
	
	/*
	@Autowired
	ElasticsearchOperations elasticsearchTemplate;
	*/
	
	@Autowired
	private LoadingMonitorRepository loadingMonitorRepository;
	
	final String index = "kpis";
	
	final String type = "generic";
	
	
	/*
	@Override
	public LoadingStatusDTO getLoadingMonitor(String organisation, Timestamp startDate, Timestamp endDate) throws ProcessingException {
		final BoolQueryBuilder builder = boolQuery()
				.must(matchQuery("isStandard", Boolean.TRUE))
				.must(rangeQuery("datetime").gte(startDate).lte(endDate))
				;
		
		if(!StringUtils.isBlank(organisation))
			builder.must(matchQuery("organisation", organisation));

		TopHitsBuilder kpiParameterValueBuilder = AggregationBuilders.topHits("kpiParameters")
				.setSize(1)
				.setFetchSource(new String[]{"parameters"}, null);
		
		final AbstractAggregationBuilder aggregationBuilder =
				AggregationBuilders.terms("group_by_name").field("name").size(1000)
	        	.subAggregation(
	        		AggregationBuilders.terms("group_by_datetime").field("datetime").size(1000)
	        		.subAggregation(kpiParameterValueBuilder)
	        	);
		
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
		        .withIndices(index)
		        .withTypes(type)
		        .withQuery(builder)
		        .addAggregation(aggregationBuilder)		        		
		        .build();
		
		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				return response.getAggregations();
			}
		});
		LoadingStatusDTO dto = new LoadingStatusDTO();
		
		Terms nameTerms = aggregations.get("group_by_name");
		if(nameTerms == null) return dto;
		
		final List<KpiBucket> kpiBuckets = new ArrayList<KpiBucket>();
		List<Bucket> nameBuckets = nameTerms.getBuckets();
		
		for(Bucket nameBucket: nameBuckets){
			// Create KPI Bucket
			KpiBucket kpiBucket = new KpiBucket();
			kpiBucket.setName(nameBucket.getKeyAsString());
			kpiBuckets.add(kpiBucket);
			
			Aggregations nameAggregations = nameBucket.getAggregations();
			Terms datetimeTerms = nameAggregations.get("group_by_datetime");
			if(datetimeTerms == null) return dto;
			
			List<Bucket> datetimeBuckets = datetimeTerms.getBuckets();
			for(Bucket datetimeBucket: datetimeBuckets){
				Aggregations datetimeAggregations = datetimeBucket.getAggregations();
				TopHits kpiParameters = datetimeAggregations.get("kpiParameters");
				
				if(kpiParameters == null) return dto;
				
				SearchHits kpiParameterHits = kpiParameters.getHits();
				if(kpiParameterHits == null) return dto;
				
				kpiParameterHits.forEach( searchHit -> {
					Map<String, Object> source = searchHit.getSource();
					if(source == null) return;
					try{	
						
						@SuppressWarnings("unchecked")
						ArrayList<Map<String, Object>> parameters = (ArrayList<Map<String, Object>>)source.get("parameters");
						
						for(Map<String, Object> parameter: parameters){
							String name = (String)parameter.get("name");
							Double value = (Double)parameter.get("value");
							if(value != Double.NaN){
								kpiBucket.updateParameterDailyBucket(name, datetimeBucket.getKeyAsString());
							}
						}
						
							
						 	
					}catch(Exception e){
						LOG.error("Exception when processing parameters [Exception:{}]", e.toString());
					}
				});
			}
		}
		
		dto.updateFromBucket(kpiBuckets);
		return dto;
	}
*/

	@Override
	public LoadingStatusDTO generateLoadingMonitor(String organisation, String date)
			throws ProcessingException {
		Iterable<LoadingMonitorEntity> loadingMonitors = loadingMonitorRepository.findByOrganisationAndDate(organisation, date);
		
		LoadingStatusDTO dto = new LoadingStatusDTO();
		if(loadingMonitors == null) return dto;
		
		List<KpiBucket> buckets = KpiBucketCollector.generateBuckets(loadingMonitors);
		if(buckets != null && buckets.size() > 0) dto.updateFromBucket(buckets);
		
		return dto;
	}
	
	
}
