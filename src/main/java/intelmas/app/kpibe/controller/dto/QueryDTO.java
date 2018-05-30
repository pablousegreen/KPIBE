package intelmas.app.kpibe.controller.dto;

import intelmas.app.kpibe.controller.dto.model.QueryResponse;

public class QueryDTO extends BaseDTO {

	private QueryResponse response;
	
	public QueryDTO() {
		super("0000", "OK");
	}
	
	public QueryResponse getResponse() {
		return response;
	}
	
	public void setResponse(QueryResponse response) {
		this.response = response;
	}
	
}
