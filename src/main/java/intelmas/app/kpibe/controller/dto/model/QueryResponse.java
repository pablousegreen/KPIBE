package intelmas.app.kpibe.controller.dto.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponse {

	private Set<String> hits;
	
	public Set<String> getHits() {
		return hits;
	}
	
	public void setHits(Set<String> hits) {
		this.hits = hits;
	}
}
