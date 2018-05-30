package intelmas.app.kpibe.controller.dto;

import java.util.HashSet;
import java.util.Set;

import intelmas.app.kpibe.controller.dto.model.TypeCompleteStatus;
public class TypeCompleteStatusesDTO extends BaseDTO {

	private Set<TypeCompleteStatus> result;
	
	public TypeCompleteStatusesDTO() {
		super("0000", "OK");
	}
	
	public void addResult(TypeCompleteStatus status){
		if(this.result == null) this.result = new HashSet<TypeCompleteStatus>();
		result.add(status);
	}
	
	public Set<TypeCompleteStatus> getResult() {
		return result;
	}
	
	public void setResult(Set<TypeCompleteStatus> result) {
		this.result = result;
	}
	
	
	
}
