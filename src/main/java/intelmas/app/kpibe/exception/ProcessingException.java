package intelmas.app.kpibe.exception;

import intelmas.app.kpibe.controller.dto.BaseDTO;

public class ProcessingException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4218529259818816760L;
	private String code;
	private String description;
	
	public ProcessingException(String code, String description){
		super(description);
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
	
	public BaseDTO generateBaseDTO(){
		return new BaseDTO(this.getCode(), this.getDescription());
	}
	
}
