package intelmas.app.kpibe.repository.elastic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import intelmas.app.kpibe.exception.ProcessingException;
import intelmas.app.kpibe.model.elastic.KpiDocument;

@Service
public class KpiRepository{
	
	@Autowired
	private RestHighLevelClient client;
	
	public RestHighLevelClient getClient() {
		return client;
	}
	
	public void setClient(RestHighLevelClient client) {
		this.client = client;
	}
	
	public SearchResponse search(SearchRequest searchRequest) throws ProcessingException{
		try {
			return client.search(searchRequest, new Header[0]);
		} catch (IOException e) {
			throw new ProcessingException("2000", "Exception when searching [Exception:" + e.toString() + "]");
		}
	}
	
	public List<KpiDocument> generateFromSearchResponse(SearchResponse searchResponse) throws ProcessingException{
		if(searchResponse == null || searchResponse.getHits() == null) throw new ProcessingException("4000", "Unable to get response from elasticsearch");
		
		return generateFromSearchHits(searchResponse.getHits());
	}
	
	public List<KpiDocument> generateFromSearchHits(SearchHits searchHits) throws ProcessingException{
		if(searchHits == null) throw new ProcessingException("4000", "Unable to find search hits");
		
		SearchHit[] hits = searchHits.getHits();
		if(hits == null) throw new ProcessingException("4000", "Unable to find search hits");
		
		return Arrays.stream(hits)
		  .map(searchHit -> new KpiDocument(searchHit))
		  .collect(Collectors.toList());
	}	
	
	public List<KpiDocument> queryForList(SearchRequest searchRequest) throws ProcessingException{
		SearchResponse searchResponse = search(searchRequest);
		return generateFromSearchResponse(searchResponse);
	}
	
}
