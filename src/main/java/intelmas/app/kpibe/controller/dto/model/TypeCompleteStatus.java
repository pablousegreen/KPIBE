package intelmas.app.kpibe.controller.dto.model;

public class TypeCompleteStatus {

	private String type;
	private String status;
	
	public TypeCompleteStatus(){
	}
	
	public TypeCompleteStatus(String type, String status){
		this.setType(type);
		this.setStatus(status);
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
