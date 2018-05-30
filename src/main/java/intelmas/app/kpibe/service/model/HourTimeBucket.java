package intelmas.app.kpibe.service.model;

public class HourTimeBucket {

	private Boolean bucket00Status = Boolean.FALSE;
	private Boolean bucket15Status = Boolean.FALSE;
	private Boolean bucket30Status = Boolean.FALSE;
	private Boolean bucket45Status = Boolean.FALSE;
	
	public Boolean getBucket00Status() {
		return bucket00Status;
	}
	
	public void setBucket00Status(Boolean bucket00Status) {
		this.bucket00Status = bucket00Status;
	}
	
	public Boolean getBucket15Status() {
		return bucket15Status;
	}
	
	public void setBucket15Status(Boolean bucket15Status) {
		this.bucket15Status = bucket15Status;
	}

	public Boolean getBucket30Status() {
		return bucket30Status;
	}

	public void setBucket30Status(Boolean bucket30Status) {
		this.bucket30Status = bucket30Status;
	}

	public Boolean getBucket45Status() {
		return bucket45Status;
	}

	public void setBucket45Status(Boolean bucket45Status) {
		this.bucket45Status = bucket45Status;
	}
	
	public void updateStatus(Integer minute) {
		if(minute == 0)
			this.setBucket00Status(Boolean.TRUE);
		else if(minute == 15)
			this.setBucket15Status(Boolean.TRUE);
		else if(minute == 30)
			this.setBucket30Status(Boolean.TRUE);
		else if(minute == 45)
			this.setBucket45Status(Boolean.TRUE);
	}
	
	public String getStatus(){
		if(this.getBucket00Status() && this.getBucket15Status() && this.getBucket30Status() && this.getBucket45Status())
			return "Loaded";
		else if(!this.getBucket00Status() && !this.getBucket15Status() && !this.getBucket30Status() && !this.getBucket45Status())
			return "Not Loaded";
		else
			return "Partially Loaded";
	}
}
