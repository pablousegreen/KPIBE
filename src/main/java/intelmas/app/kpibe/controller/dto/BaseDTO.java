package intelmas.app.kpibe.controller.dto;

public class BaseDTO {

	protected String code;
	protected String description;
	
	public BaseDTO(String code, String description) {
		this.setCode(code);
		this.setDescription(description);
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[code:").append(code).append("]");
		sb.append("[description:").append(description).append("]");
		
		return sb.toString();
	}
}
